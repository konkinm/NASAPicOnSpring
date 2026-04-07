package space.maxkonkin.nasapicbot.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MessageServiceTest {

    @Test
    fun testGetMessage() {
        val messageService = MessageService()
        
        val helpText = messageService.getMessage("help.text", "ru")
        assert(helpText.contains("Привет, я бот NASA!"))
        
        val errorText = messageService.getMessage("error.unsupported_command", "ru")
        assertEquals("Команда не поддерживается.", errorText)
        
        val helpTextEn = messageService.getMessage("help.text", "en")
        assert(helpTextEn.contains("Hi, I'm a NASA bot!"))
        
        val errorTextEn = messageService.getMessage("error.unsupported_command", "en")
        assertEquals("Command not supported.", errorTextEn)
    }
    
    @Test
    fun testGetScheduleMessage() {
        val messageService = MessageService()
        
        val scheduleOn = messageService.getScheduleMessage(true, "ru")
        assertEquals("Отправка по расписанию: вкл", scheduleOn)
        
        val scheduleOff = messageService.getScheduleMessage(false, "ru")
        assertEquals("Отправка по расписанию: выкл", scheduleOff)
    }
    
    @Test
    fun testGetTranslationMessage() {
        val messageService = MessageService()
        
        val translationEnabled = messageService.getTranslationMessage(true, "ru")
        assertEquals("Перевод включен", translationEnabled)
        
        val translationDisabled = messageService.getTranslationMessage(false, "ru")
        assertEquals("Перевод выключен", translationDisabled)
    }
}