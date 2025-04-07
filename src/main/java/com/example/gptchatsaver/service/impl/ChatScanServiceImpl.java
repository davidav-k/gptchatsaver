package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;
import com.example.gptchatsaver.repository.AiModelRepository;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.repository.ChatSessionRepository;
import com.example.gptchatsaver.service.ChatScanService;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatScanServiceImpl implements ChatScanService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiModelRepository aiModelRepository;

    @Value("${chrome-port}")
    private String chromePort;

    public void scanChat() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:" + chromePort);
        WebDriver driver = new ChromeDriver(options);

        try {
            WebDriver chatDriver = switchToChatGPTWindow(driver);
            AIModel aiModel = createAIModel(chatDriver);
            ChatSession chatSession = createChatSession(aiModel);
            createChatMessages(chatDriver, chatSession);
        } catch (Exception e) {
            throw new RuntimeException("Error scan chat", e);
        }
    }

    private WebDriver switchToChatGPTWindow(WebDriver driver) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("chatgpt")) {
                log.info("Found window: {}", currentUrl);
                return driver;
            }
        }
        throw new RuntimeException("Not found ChatGPT's window");
    }

    private AIModel createAIModel(WebDriver driver) {
        List<WebElement> elementVersionChats = driver.findElements(By.cssSelector("button[aria-haspopup=menu] div span"));
        AIModel aiModel = AIModel.builder()
                .modelSlug(driver.findElement(By.cssSelector("div[data-message-model-slug]"))
                                .getAttribute("data-message-model-slug"))
                .version(elementVersionChats.get(1).getText().trim())
                .provider("OpenAI")
                .build();
        return aiModelRepository.save(aiModel);
    }

    private ChatSession createChatSession(AIModel aiModel) {
        ChatSession chatSession = ChatSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();
        return chatSessionRepository.save(chatSession);
    }

    private void createChatMessages(WebDriver driver, ChatSession chatSession) {
        String titleChat = driver.getTitle();

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

            while (true) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) break;
                lastHeight = newHeight;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article[data-testid]")));

            List<WebElement> articleElements = driver.findElements(By.cssSelector("article[data-testid]"));
            log.info("🔍 Найдено article-блоков: {}", articleElements.size());

            for (int i = 0, turnIndex = 0; i < articleElements.size() - 1; i += 2, turnIndex++) {
                WebElement questionBlock = articleElements.get(i);
                WebElement answerBlock = articleElements.get(i + 1);

                try {
                    // Вопрос
                    String question = questionBlock.getText().trim();
                    String questionId = questionBlock.findElement(By.cssSelector("div[data-message-author-role='user']"))
                            .getAttribute("data-message-id");

                    // Ответ
                    WebElement markdown = answerBlock.findElement(By.cssSelector("div.markdown"));
                    String answer = markdown.getText().trim().replaceAll("\\s+", " ");
                    String answerHtml = cleanAnswerHtml(markdown.getAttribute("outerHTML"));



                    String answerId = answerBlock.findElement(By.cssSelector("div[data-message-author-role='assistant']"))
                            .getAttribute("data-message-id");

                    if (!messageExists(titleChat, question)) {
                        ChatMessage chatMessage = ChatMessage.builder()
                                .chatSession(chatSession)
                                .aiModel(chatSession.getAiModel())
                                .title(titleChat)
                                .question(question)
                                .questionId(questionId)
                                .answer(answer)
                                .answerHtml(answerHtml)
                                .answerId(answerId)
                                .turnIndex(turnIndex)
                                .timestamp(LocalDateTime.now())
                                .build();
                        chatMessageRepository.save(chatMessage);
                        log.info("💾 Сохранено сообщение: {}", question.substring(0, Math.min(20, question.length())) + "...");
                    }
                } catch (Exception e) {
                    log.warn("Ошибка при обработке пар блоков {} и {}: {}", i, i + 1, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений: {}", e.getMessage(), e);
        }
    }

    private boolean messageExists(String title, String question) {
        return chatMessageRepository.existsByTitleAndQuestion(title, question);
    }

    public String cleanAnswerHtml(String rawHtml) {
        Document doc = Jsoup.parseBodyFragment(rawHtml);

        // Удаляем элементы по классу
        doc.select("div.sticky").remove(); // Кнопки "Copy", "Edit"
        doc.select("div[aria-label=Copy]").remove(); // На всякий случай

        // Можно добавить другие div'ы, если нужно
        return doc.body().html();
    }
}
