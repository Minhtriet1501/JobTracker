package com.jobtracker.backend.notification;

import com.jobtracker.backend.user.User;
import com.jobtracker.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<NotificationResponse> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return notificationRepository.findByUserOrderBySentAtDesc(user).stream().map(n -> {
            NotificationResponse res = new NotificationResponse();
            res.setId(n.getId());
            res.setType(n.getType());
            res.setMessage(n.getMessage());
            res.setIsRead(n.getIsRead());
            res.setSentAt(n.getSentAt());
            return res;
        }).toList() ;
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok(Map.of("massage", "Marked as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markReadAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
        return ResponseEntity.ok(Map.of("massage", "All marked as read"));
    }

}
