package com.jobtracker.backend.user;


import com.jobtracker.backend.common.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }

    public User register(String email, String password, String name) {
        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        return userRepository.save(user);
    }
    private UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setResumeText(user.getResumeText());
        return userResponse;
    }

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));
        return toResponse(user);
    }

    public UserResponse updateProfile(String email, UserUpdateRequest request) {
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));
        if(request.getName() != null) {
            user.setName(request.getName());
        }
        if(request.getResumeText() != null) {
            user.setResumeText(request.getResumeText());
        }
        userRepository.save(user);
        return toResponse(user);
    }



}
