package com.jobtracker.backend.user;

import com.jobtracker.backend.common.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
public class UserServiceTest {
    String nullEmail = "abc";
    String email = "test@gmail.com";
    String password = "123455";
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("hashed_password");
        mockUser.setName("Triet");
    }

    @Test
    void loadUserByUsernameUserExistsReturnUserDetails() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        UserDetails result = userService.loadUserByUsername(email);
        assertEquals(email, result.getUsername());
    }

    @Test
    void  loadUserByUsernameUserNotFoundThrowsException() {
        when(userRepository.findByEmail(nullEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(nullEmail));
    }

    @Test
    void registerNewEmailReturnUser() {
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.register(email, password, "Triet");
        assertEquals(email, result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerDuplicateEmailThrowsException() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(email, password, "Triet"));
    }

    @Test
    void updateProfileUpdatesNameAndResumeText() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("New Name");
        request.setResumeText("My resume");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.updateProfile(email, request);

        assertEquals("New Name", mockUser.getName());
        assertEquals("My resume", mockUser.getResumeText());
        verify(userRepository).save(mockUser);
    }

    @Test
    void updateProfileUserNotFoundThrowsException() {
        UserUpdateRequest request = new UserUpdateRequest();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.updateProfile(email, request));
    }

}
