package com.TaskTracker.controller;

import com.TaskTracker.dto.LoginDto;
import com.TaskTracker.dto.RegisterDto;
import com.TaskTracker.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldSaveAccount_WhenValidRegisterDtoIsProvided() {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testUser");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password123");

        // When
        accountController.register(registerDto);

        // Then
        verify(accountService, times(1)).saveAccount(registerDto);
    }

    @Test
    void updateLoginData_ShouldReturnOk_WhenLoginDtoIsValid() {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("newPassword123");

        // When
        ResponseEntity<String> response = accountController.updateLoginData(loginDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login data updated successfully", response.getBody());
        verify(accountService, times(1)).changeLoginData(loginDto);
    }

    @Test
    void updateLoginData_ShouldReturnBadRequest_WhenExceptionOccurs() {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("invalidPassword");

        doThrow(new RuntimeException("Failed to update")).when(accountService).changeLoginData(loginDto);

        // When
        ResponseEntity<String> response = accountController.updateLoginData(loginDto);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Failed to update login data"));
        verify(accountService, times(1)).changeLoginData(loginDto);
    }
}
