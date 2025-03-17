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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatScanServiceImpl implements ChatScanService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiModelRepository aiModelRepository;

    @Override
    public void scanChat() {

        WebDriverManager.chromedriver()
                .driverVersion("134.0.6998.89")
                .setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:9222");
        WebDriver driver = new ChromeDriver(options);

        try {

            Set<String> windowHandles = driver.getWindowHandles();
            boolean chatgptFound = false;

            for (String handle : windowHandles) {
                driver.switchTo().window(handle);
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("chatgpt")) {
                    chatgptFound = true;
                    System.out.println("Найдено окно с ChatGPT: " + currentUrl);
                    break;
                }
            }
            if (!chatgptFound) {
                throw new RuntimeException("Не найдено окно с ChatGPT");
            }

            List<WebElement> elementVersionChats = driver.findElements(By.cssSelector("button[aria-haspopup=menu] div span"));

            String sessionId = UUID.randomUUID().toString();
            AIModel aiModel = new AIModel();
            aiModel.setName("ChatGPT");
            aiModel.setVersion(elementVersionChats.get(1).getText().trim());
            aiModel.setProvider("OpenAI");
            aiModel = aiModelRepository.save(aiModel);
            System.out.println(aiModel);

            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setCreatedAt(LocalDateTime.now());
            chatSession.setAiModel(aiModel);

            chatSession = chatSessionRepository.save(chatSession);
            System.out.println(chatSession.getSessionId());

            List<WebElement> articleElements = driver.findElements(By.cssSelector("article[data-testid]"));
            System.out.println(articleElements.size());


            for (int i = 0; i < articleElements.size(); i += 2) {
                WebElement questionElement = articleElements.get(i).findElement(By.xpath(".//div//div//div//div//div//div//div//div//div//div"));
                String question = questionElement.getText();
                WebElement answerElement = articleElements.get(i + 1).findElement(By.cssSelector("div.markdown"));
                String answer = answerElement.getText();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setChatSession(chatSession);
                chatMessage.setSender("ChatGPT");
                chatMessage.setQuestion(question);
                chatMessage.setAnswer(answer);
                chatMessage.setTimestamp(LocalDateTime.now());

                chatMessageRepository.save(chatMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сканировании чата", e);
        } finally {
            // Не вызываем driver.quit(), чтобы не закрывать уже запущенный экземпляр Chrome,
            // к которому мы подключились через отладку.
        }
    }
}
