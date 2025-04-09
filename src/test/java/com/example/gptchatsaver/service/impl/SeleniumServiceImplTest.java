package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatSession;
import com.example.gptchatsaver.exception.ScanningException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.example.gptchatsaver.constant.ConstantsForScanning.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class SeleniumServiceImplTest {

    @InjectMocks
    private SeleniumServiceImpl seleniumService;

    @Mock
    private WebDriver driver;

    @Mock
    private WebElement modelVersionElement;

    @Mock
    private WebElement modelSlugElement;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(seleniumService, "chromePort", "9222");
    }

    @Test
    void initDriverSuccessfully() {
        try (MockedStatic<WebDriverManager> webDriverManagerMock = mockStatic(WebDriverManager.class)) {

            WebDriverManager webDriverManagerInstance = mock(WebDriverManager.class);
            webDriverManagerMock.when(WebDriverManager::chromedriver).thenReturn(webDriverManagerInstance);
            doNothing().when(webDriverManagerInstance).setup();

            SeleniumServiceImpl spy = spy(seleniumService);

            ChromeDriver chromeDriver = mock(ChromeDriver.class);

            doReturn(chromeDriver).when(spy).createChromeDriver(any());
            doReturn(chromeDriver).when(spy).switchToRequiredWindow(any());

            WebDriver result = spy.initDriver();
            assertNotNull(result);
        }
    }

    @Test
    void initDriverThrowsScanningExceptionWhenWebDriverExceptionOccurs() {
        try (MockedStatic<WebDriverManager> webDriverManagerMock = mockStatic(WebDriverManager.class)) {
            WebDriverManager webDriverManagerInstance = mock(WebDriverManager.class);
            webDriverManagerMock.when(WebDriverManager::chromedriver).thenReturn(webDriverManagerInstance);
            doNothing().when(webDriverManagerInstance).setup();

            SeleniumServiceImpl spy = spy(seleniumService);
            doThrow(new WebDriverException("Failed to start driver")).when(spy).createChromeDriver(any());

            assertThrows(ScanningException.class, () -> spy.initDriver());
        }
    }

    @Test
    void initDriverThrowsScanningExceptionWhenChatGPTWindowNotFound() {
        try (MockedStatic<WebDriverManager> webDriverManagerMock = mockStatic(WebDriverManager.class)) {
            WebDriverManager webDriverManagerInstance = mock(WebDriverManager.class);
            webDriverManagerMock.when(WebDriverManager::chromedriver).thenReturn(webDriverManagerInstance);
            doNothing().when(webDriverManagerInstance).setup();

            SeleniumServiceImpl spy = spy(seleniumService);
            ChromeDriver chromeDriver = mock(ChromeDriver.class);
            doReturn(chromeDriver).when(spy).createChromeDriver(any());

            doThrow(new ScanningException("ChatGPT window not found")).when(spy).switchToRequiredWindow(any());

            assertThrows(ScanningException.class, () -> spy.initDriver());
        }
    }
    @Test
    void extractAIModelInfoSuccessfully() {
        List<WebElement> modelVersionElements = List.of(mock(WebElement.class), modelVersionElement);
        when(modelVersionElement.getText()).thenReturn("GPT-4o");

        when(driver.findElements(By.cssSelector(SELECTOR_MODEL_VERSION))).thenReturn(modelVersionElements);
        when(driver.findElement(By.cssSelector(SELECTOR_MODEL_SLUG))).thenReturn(modelSlugElement);
        when(modelSlugElement.getAttribute(ATTRIBUTE_MODEL_SLUG)).thenReturn("gpt-4o");

        AIModel model = seleniumService.extractAIModelInfo(driver);

        assertNotNull(model);
        assertEquals("gpt-4o", model.getModelSlug());
        assertEquals("GPT-4o", model.getVersion());
        assertEquals(PROVIDER, model.getProvider());
    }

    @Test
    void extractAIModelInfoThrowsExceptionWhenWebDriverIsNull() {
        assertThrows(ScanningException.class, () -> seleniumService.extractAIModelInfo(null));
    }

    @Test
    void extractAIModelInfoThrowsExceptionWhenModelVersionNotFound() {
        List<WebElement> modelVersionElements = List.of(mock(WebElement.class));
        when(driver.findElements(By.cssSelector(SELECTOR_MODEL_VERSION))).thenReturn(modelVersionElements);

        assertThrows(ScanningException.class, () -> seleniumService.extractAIModelInfo(driver));
    }

    @Test
    void extractChatMessagesThrowsExceptionWhenWebDriverIsNull() {
        assertThrows(ScanningException.class, () ->
                seleniumService.extractChatMessages(mock(ChatSession.class), null));
    }

    @Test
    void closeDriverSuccessfully() {
        seleniumService.closeDriver(driver);
        verify(driver, times(1)).quit();
    }

    @Test
    void closeDriverHandlesException() {
        doThrow(new WebDriverException("Failed to close")).when(driver).quit();

        seleniumService.closeDriver(driver);
    }

    @Test
    void closeDriverHandlesNullDriver() {
        seleniumService.closeDriver(null);
    }

    protected ChromeDriver createChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

}