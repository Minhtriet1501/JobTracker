package com.jobtracker.backend.notification;


import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.applicaition.ApplicationRepository;
import com.jobtracker.backend.user.User;
import com.jobtracker.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void scanDeadlines() {
        log.info(">>> scanDeadlines running at {}", LocalDate.now());
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Application> applications = applicationRepository.findAll();

        for(Application app : applications) {
            if(app.getDeadLine() != null && app.getDeadLine().equals(tomorrow)) {
                User user = app.getUser();
                String message = "Deadline reminder: " + app.getCompanyName() + " - " + app.getPosition() + " is due tomorrow";
                try {
                    emailService.sendEmail(user.getEmail(), "Deadline reminder", message);
                    log.info(">>> Email sent to {}", user.getEmail());
                }
                catch(Exception e) {
                    log.error(">>> Email FAILED: {}", e.getMessage(), e);
                }

                Notification notification = new Notification();
                notification.setUser(user);
                notification.setType(NotificationType.DEADLINE_REMINDER);
                notification.setMessage(message);
                notificationRepository.save(notification);
            }
        }
    }
}
