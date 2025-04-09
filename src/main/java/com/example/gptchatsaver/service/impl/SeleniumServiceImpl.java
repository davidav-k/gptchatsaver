package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;

import static com.example.gptchatsaver.constant.ConstantsForScanning.*;

import com.example.gptchatsaver.exception.ScanningException;
import com.example.gptchatsaver.service.SeleniumService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeleniumServiceImpl implements SeleniumService {

    @Value("${chrome-port}")
    private String chromePort;

    @Override
    public WebDriver initDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:" + chromePort);
        try {
            WebDriver driver = createChromeDriver(options);
            return switchToRequiredWindow(driver);
        } catch (WebDriverException ex) {
            throw new ScanningException("Failed to initialize WebDriver", ex);
        }
    }

    protected WebDriver switchToRequiredWindow(WebDriver driver) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            String currentUrl = driver.getCurrentUrl();
            assert currentUrl != null;
            if (currentUrl.contains(CHATGPT)) {
                log.info("Found ChatGPT window: {}", currentUrl);
                return driver;
            }
        }
        throw new ScanningException("ChatGPT window not found");
    }

    @Override
    public AIModel extractAIModelInfo(WebDriver driver) {
        if (driver == null) {
            throw new ScanningException("WebDriver not initialized");
        }

        try {
            List<WebElement> elementVersionChats = driver.findElements(By.cssSelector(SELECTOR_MODEL_VERSION));
            if (elementVersionChats.size() < 2) {
                throw new ScanningException("Could not find model version element");
            }

            String modelVersion = elementVersionChats.get(1).getText().trim();
            log.info("Detected ChatGPT model version: {}", modelVersion);

            return AIModel.builder()
                    .modelSlug(driver.findElement(By.cssSelector(SELECTOR_MODEL_SLUG))
                            .getAttribute(ATTRIBUTE_MODEL_SLUG))
                    .version(modelVersion)
                    .provider(PROVIDER)
                    .build();
        } catch (Exception ex) {
            throw new ScanningException("Failed to extract AI model information", ex);
        }
    }

    @Override
    public List<ChatMessage> extractChatMessages(ChatSession chatSession, WebDriver driver) {
        if (driver == null) {
            throw new ScanningException("WebDriver not initialized");
        }

        String titleChat = driver.getTitle();
        List<ChatMessage> messages = new ArrayList<>();

        try {
            scrollToBottom(driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(SELECTOR_ARTICLE_ELEMENT)));

            List<WebElement> articleElements = driver.findElements(By.cssSelector(SELECTOR_ARTICLE_ELEMENT));
            log.info("Found {} article blocks", articleElements.size());

            processArticleElements(chatSession, titleChat, articleElements, messages);

            return messages;
        } catch (Exception ex) {
            throw new ScanningException("Failed to extract chat messages", ex);
        }
    }

    private void scrollToBottom(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

        // Scroll with timeout to prevent infinite loops
        int maxScrollAttempts = 20;
        int scrollAttempts = 0;

        while (scrollAttempts < maxScrollAttempts) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

            try {
                // Wait for content to load
                Duration scrollWaitTime = Duration.ofMillis(300);
                long finalLastHeight = lastHeight;
                new WebDriverWait(driver, scrollWaitTime)
                        .until(webDriver -> {
                            long newHeight = (long) ((JavascriptExecutor) webDriver)
                                    .executeScript("return document.body.scrollHeight");
                            return newHeight > finalLastHeight;
                        });

                long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) {
                    // Height didn't change, we're at the bottom
                    break;
                }

                lastHeight = newHeight;
            } catch (TimeoutException ex) {
                // If timeout occurs, we might be at the bottom
                scrollAttempts++;
                if (scrollAttempts >= 3) {
                    // After 3 attempts without height change, assume we're at bottom
                    break;
                }
            }
        }
    }

    private void processArticleElements(ChatSession chatSession, String titleChat,
                                        List<WebElement> articleElements, List<ChatMessage> messages) {
        for (int i = 0, turnIndex = 0; i < articleElements.size() - 1; i += 2, turnIndex++) {
            try {
                if (i + 1 >= articleElements.size()) {
                    log.warn("Skipping incomplete Q&A pair at index {}", i);
                    continue;
                }

                WebElement questionBlock = articleElements.get(i);
                WebElement answerBlock = articleElements.get(i + 1);

                String question = questionBlock.getText().trim();
                String questionId;
                try {
                    questionId = questionBlock.findElement(By.cssSelector(SELECTOR_QUESTION_ID))
                            .getAttribute(ATTRIBUTE_QUESTION_ID);
                } catch (NoSuchElementException e) {
                    log.warn("Could not find question ID at index {}, using fallback", i);
                    questionId = "unknown-" + System.currentTimeMillis();
                }

                WebElement webElementAnswer;
                try {
                    webElementAnswer = answerBlock.findElement(By.cssSelector(SELECTOR_ANSWER));
                } catch (NoSuchElementException e) {
                    log.warn("Could not find markdown element at index {}, skipping", i + 1);
                    continue;
                }

                String answer = webElementAnswer.getText().trim().replaceAll("\\s+", " ");
                String answerHtml = cleanAnswerHtml(webElementAnswer.getAttribute("outerHTML"));

                String answerId;
                try {
                    answerId = answerBlock.findElement(By.cssSelector(SELECTOR_ANSWER_ID))
                            .getAttribute(ATTRIBUTE_ANSWER_ID);
                } catch (NoSuchElementException e) {
                    log.warn("Could not find answer ID at index {}, using fallback", i + 1);
                    answerId = "unknown-" + System.currentTimeMillis();
                }

                ChatMessage chatMessage = ChatMessage.builder()
                        .chatSession(chatSession)
                        .title(titleChat)
                        .aiModel(chatSession.getAiModel())
                        .question(question)
                        .questionId(questionId)
                        .answer(answer)
                        .answerHtml(answerHtml)
                        .answerId(answerId)
                        .turnIndex(turnIndex)
                        .timestamp(LocalDateTime.now())
                        .build();

                messages.add(chatMessage);
                log.debug("Extracted message: {}", question.substring(0, Math.min(20, question.length())) + "...");
            } catch (StaleElementReferenceException e) {
                log.warn("Element became stale during processing at index {}", i);
            } catch (Exception e) {
                log.warn("Error processing article blocks at index {}: {}", i, e.getMessage());
            }
        }
    }

    private String cleanAnswerHtml(String rawHtml) {
        Document doc = Jsoup.parseBodyFragment(rawHtml);

        removeUIElements(doc);

        return doc.body().html();
    }

    private void removeUIElements(Document doc) {
        doc.select("div.sticky").remove();
        doc.select("div[aria-label=Copy]").remove();
        doc.select("button").remove();
    }

    @Override
    public void closeDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver closed successfully");
            } catch (Exception e) {
                log.warn("Error closing WebDriver: {}", e.getMessage());
            } finally {
                driver = null;
            }
        }
    }

    protected ChromeDriver createChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }
}
