package com.TaskTracker.service;

import com.TaskTracker.dto.LoginDto;
import com.TaskTracker.dto.RegisterDto;
import com.TaskTracker.entity.Account;
import com.TaskTracker.repo.AccountRepo;
import com.TaskTracker.service.JWTService;
import com.TaskTracker.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private RegisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mockowanie danych użytkownika
        account = new Account("TestUser", "test@example.com", "password");
        account.setId(1L);

        registerDto = new RegisterDto();
        registerDto.setUsername("TestUser");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");
    }

    @Test
    public void loadUserByUsername_ShouldReturnUserDetails() {
        // Given
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));

        // When
        UserDetails userDetails = accountService.loadUserByUsername(account.getEmail());

        // Then
        assertEquals(account.getEmail(), userDetails.getUsername());
        assertEquals(account.getPassword(), userDetails.getPassword());
        verify(accountRepo, times(1)).findByEmail(account.getEmail());
    }

    @Test
    public void loadUserByUsername_ShouldThrowUsernameNotFoundException() {
        // Given
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(account.getEmail()));
    }

    @Test
    public void saveAccount_ShouldSaveNewAccount() {
        // Given
        when(accountRepo.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        // When
        accountService.saveAccount(registerDto);

        // Then
        verify(accountRepo, times(1)).save(any(Account.class));
    }

    @Test
    public void saveAccount_ShouldThrowIllegalArgumentExceptionIfEmailExists() {
        // Given
        when(accountRepo.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(account));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> accountService.saveAccount(registerDto));
    }

    @Test
    public void getAuthenticatedAccount_ShouldReturnAuthenticatedAccount() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(account.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));

        // When
        Account authenticatedAccount = accountService.getAuthenticatedAccount();

        // Then
        assertEquals(account.getEmail(), authenticatedAccount.getEmail());
        verify(accountRepo, times(1)).findByEmail(account.getEmail());
    }

    @Test
    public void getAuthenticatedAccount_ShouldThrowRuntimeExceptionIfAccountNotFound() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(account.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> accountService.getAuthenticatedAccount());
    }

    @Test
    public void verify_ShouldReturnJWTTokenIfAuthenticated() {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(loginDto.getEmail())).thenReturn("jwtToken");

        // When
        String token = accountService.verify(loginDto);

        // Then
        assertEquals("jwtToken", token);
    }

    @Test
    public void verify_ShouldReturnFailIfAuthenticationFails() {
        // Given
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // When
        String token = accountService.verify(loginDto);

        // Then
        assertEquals("Fail", token);
    }

    @Test
    public void getById_ShouldReturnAccount() {
        // Given
        when(accountRepo.findById(account.getId())).thenReturn(Optional.of(account));

        // When
        Account foundAccount = accountService.getById(account.getId());

        // Then
        assertEquals(account.getId(), foundAccount.getId());
    }

    @Test
    public void getById_ShouldThrowRuntimeExceptionIfAccountNotFound() {
        // Given
        when(accountRepo.findById(account.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> accountService.getById(account.getId()));
    }

    @Test
    public void changeLoginData_ShouldUpdateAccountData() {
        // Given
        LoginDto newLoginDto = new LoginDto();
        newLoginDto.setEmail("new-email@example.com");
        newLoginDto.setPassword("newPassword");
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
        when(accountRepo.findByEmail(newLoginDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newLoginDto.getPassword())).thenReturn("encodedNewPassword");

        // When
        accountService.changeLoginData(newLoginDto);

        // Then
        assertEquals("new-email@example.com", account.getEmail());
        verify(accountRepo, times(1)).save(account);
    }

    @Test
    public void changeLoginData_ShouldThrowIllegalArgumentExceptionIfEmailExists() {
        // Given
        LoginDto newLoginDto = new LoginDto();
        newLoginDto.setEmail("existing-email@example.com");
        newLoginDto.setPassword("newPassword");
        when(accountRepo.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
        when(accountRepo.findByEmail(newLoginDto.getEmail())).thenReturn(Optional.of(account));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> accountService.changeLoginData(newLoginDto));
    }
}
