package com.jobtracker.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    public UserResponse getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails.getUsername());
    }

    @PutMapping("/me")
    public UserResponse updateProfile(@Valid @RequestBody UserUpdateRequest request,
                                      @AuthenticationPrincipal UserDetails userDetails ) {
        return userService.updateProfile(userDetails.getUsername(), request);
    }
}
