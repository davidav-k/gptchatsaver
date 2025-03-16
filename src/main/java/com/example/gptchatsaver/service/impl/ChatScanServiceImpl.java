package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.repository.ChatSessionRepository;
import com.example.gptchatsaver.service.ChatScanService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatScanServiceImpl implements ChatScanService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    public void scanChat() {
        // Подключаемся к уже запущенному экземпляру Chrome с отладкой на порту 9222.
        // Необходимо, чтобы Chrome был запущен с флагом: --remote-debugging-port=9222
        WebDriverManager.chromedriver()
                .clearDriverCache()
                .driverVersion("134.0.6998.89")
                .setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Получаем список открытых окон
            Set<String> windowHandles = driver.getWindowHandles();
            boolean chatgptFound = false;

            // Перебираем окна и ищем вкладку, URL которой содержит "chatgpt"
            for (String handle : windowHandles) {
                driver.switchTo().window(handle);
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("chatgpt")) {
                    chatgptFound = true;
                    System.out.println("Найдено окно с ChatGPT: " + currentUrl);
                    break;
                }
            }
            // Если вкладка с ChatGPT не найдена, открываем новую вкладку с нужным URL
            if (!chatgptFound) {
                driver.switchTo().newWindow(WindowType.TAB);
                driver.get("https://chatgpt.com/c/67d2b78a-b17c-8007-af10-3da4acc35176");
                System.out.println("Открыта заданная вкладка с ChatGPT");
            }

            // Явное ожидание появления элемента на странице
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body")));

            // Пример: ищем список сообщений внутри body
            WebElement messageList = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("body ol"))
            );
            List<WebElement> messageElements = messageList.findElements(By.cssSelector("li"));

            // Создаем новую сессию чата
            String sessionId = UUID.randomUUID().toString();
            AIModel aiModel = new AIModel();
            aiModel.setName("DefaultModel");
            aiModel.setVersion("1.0");
            aiModel.setProvider("OpenAI");

            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setCreatedAt(LocalDateTime.now());
            chatSession.setAiModel(aiModel);

            chatSession = chatSessionRepository.save(chatSession);

            // Извлечение сообщений
            for (WebElement element : messageElements) {
                String messageTextQuestion;
                String messageTextAnswer;
                try {
                    // Попытка получить вопрос и ответ по разным селекторам
                    WebElement textElementQ = element.findElement(By.cssSelector("div.whitespace-pre-wrap"));
                    messageTextQuestion = textElementQ.getText();
                    WebElement textElementA = element.findElement(By.cssSelector("div.markdown"));
                    messageTextAnswer = textElementA.getText();
                } catch (Exception e) {
                    // Если не удалось разделить, берем текст целиком
                    messageTextQuestion = element.getText();
                    messageTextAnswer = element.getText();
                }
                String sender = "ChatGPT";

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setChatSession(chatSession);
                chatMessage.setSender(sender);
                chatMessage.setQuestion(messageTextQuestion);
                chatMessage.setAnswer(messageTextAnswer);
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
