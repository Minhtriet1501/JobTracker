package com.jobtracker.backend.application;

import com.jobtracker.backend.applicaition.*;
import com.jobtracker.backend.user.User;
import com.jobtracker.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User mockUser;
    private Application mockApplication;

    String email = "test@gmail.com";
    @BeforeEach
    public void setUp() throws Exception {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);

        mockApplication = new Application();
        mockApplication.setId(1L);
        mockApplication.setUser(mockUser);
        mockApplication.setCompanyName("Amazon");
        mockApplication.setPosition("Backend Intern");
        mockApplication.setStatus(ApplicationStatus.APPLIED);
    }

    @Test
    void getAllReturnUserApplications() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(applicationRepository.findByUser(mockUser)).thenReturn(List.of(mockApplication));

        List<ApplicationResponse> result = applicationService.getAllApplications(email);

        assertEquals(1, result.size());
        assertEquals("Amazon", result.get(0).getCompanyName());
    }

    @Test
    void createValidRequestReturnResponse() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Amazon");
        request.setPosition("Backend Intern");
        request.setStatus(ApplicationStatus.APPLIED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationResponse response = applicationService.createApplication(request, email);

        assertEquals("Amazon", response.getCompanyName());
        assertEquals("Backend Intern", response.getPosition());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());

        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void deleteValidOwnerDeletesApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));

        applicationService.deleteApplication(1L, email);

        verify(applicationRepository).delete(mockApplication);
    }

    @Test
    void deleteWrongOwerThrowsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));

        assertThrows(RuntimeException.class , () -> applicationService.deleteApplication(1L, "wrongEmail"));
    }

}
