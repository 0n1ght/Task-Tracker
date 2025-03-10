package com.TaskTracker.service;

import com.TaskTracker.dto.LoginDto;
import com.TaskTracker.entity.Account;
import com.TaskTracker.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    void saveAccount(RegisterDto accountDto);
    Account getAuthenticatedAccount();

    String verify(LoginDto loginDto);
    Account getById(Long id);
    void changeLoginData(LoginDto loginDto);
}