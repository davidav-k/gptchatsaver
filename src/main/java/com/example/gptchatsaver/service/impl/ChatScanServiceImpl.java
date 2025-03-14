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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatScanServiceImpl implements ChatScanService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

//    public ChatScanServiceImpl(ChatMessageRepository chatMessageRepository, ChatSessionRepository chatSessionRepository) {
//        this.chatMessageRepository = chatMessageRepository;
//        this.chatSessionRepository = chatSessionRepository;
//    }
    @Override
    public void scanChat() {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Переход на страницу чата ChatGPT
            driver.get("https://chatgpt.com/c/67d2b78a-b17c-8007-af10-3da4acc35176");

            // Явное ожидание загрузки основного контейнера чата
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main[role='main']")));

            // Находим список сообщений: контейнер <ol> внутри основного элемента
            WebElement messageList = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main[role='main'] ol")));
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

            // Сохраняем сессию (при этом AIModel можно сохранить отдельно, если требуется)
            chatSession = chatSessionRepository.save(chatSession);

            // Перебираем все найденные сообщения и сохраняем их в БД
            for (WebElement element : messageElements) {
                String messageText;
                try {
                    // Извлекаем текст из элемента с классом "markdown"
                    WebElement textElement = element.findElement(By.cssSelector("div.markdown"));
                    messageText = textElement.getText();
                } catch (Exception e) {
                    // Если не найден элемент с классом "markdown", берем текст всего элемента
                    messageText = element.getText();
                }
                // Определяем отправителя – для примера устанавливаем значение по умолчанию
                String sender = "ChatGPT";

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setChatSession(chatSession);
                chatMessage.setSender(sender);
                chatMessage.setMessage(messageText);
                chatMessage.setTimestamp(LocalDateTime.now());

                chatMessageRepository.save(chatMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сканировании чата", e);
        } finally {
            driver.quit();
        }
    }
}

