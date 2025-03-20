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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatScanServiceImpl implements ChatScanService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiModelRepository aiModelRepository;

    public void scanChat() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:9222");
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
                .name("ChatGPT")
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
        List<WebElement> articleElements = driver.findElements(By.cssSelector("article[data-testid]"));

        for (int i = 0; i < articleElements.size(); i += 2) {
            String question = articleElements.get(i)
                    .findElement(By.xpath(".//div//div//div//div//div//div//div//div//div//div"))
                    .getText();
            String answer = articleElements.get(i + 1)
                    .findElement(By.cssSelector("div.markdown"))
                    .getText()
                    .replaceAll("\\s+", " ");
            String answerHtml = Objects.requireNonNull(articleElements.get(i + 1)
                            .findElement(By.cssSelector("div.markdown"))
                            .getAttribute("outerHTML"))
                    .replaceAll("\\s+", " ");

            if (!messageExists(titleChat, question)) {
                ChatMessage chatMessage = ChatMessage.builder()
                        .chatSession(chatSession)
                        .sender("ChatGPT")
                        .title(titleChat)
                        .question(question)
                        .answer(answer)
                        .answerHtml(answerHtml)
                        .timestamp(LocalDateTime.now())
                        .build();
                chatMessageRepository.save(chatMessage);
            } else {
                log.info("Сообщение уже существует. Пропуск.");
            }
        }
    }

    private boolean messageExists(String title, String question) {
        return chatMessageRepository.existsByTitleAndQuestion(title, question);
    }
}
