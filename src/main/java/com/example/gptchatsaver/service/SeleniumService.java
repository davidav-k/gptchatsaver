package com.example.gptchatsaver.service;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface SeleniumService {

    WebDriver initDriver();

    AIModel extractAIModelInfo(WebDriver chatDriver);

    List<ChatMessage> extractChatMessages(ChatSession chatSession, WebDriver driver);

    void closeDriver(WebDriver driver);

}
