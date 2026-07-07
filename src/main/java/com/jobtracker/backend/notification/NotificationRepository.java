package com.jobtracker.backend.notification;


import com.jobtracker.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderBySentAtDesc(User user);
    long countByUserAndIsRead(User user, boolean isRead);
}
