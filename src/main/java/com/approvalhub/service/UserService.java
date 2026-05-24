package com.approvalhub.service;

import com.approvalhub.domain.entity.User;
import com.approvalhub.dto.user.UserResponseDTO;
import com.approvalhub.mapper.UserMapper;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import static com.approvalhub.domain.enums.Role.REVIEWER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User reviewer = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (reviewer.getRole().equals(REVIEWER)) {
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::userToUserResponseDTO)
                    .toList();
        }
        throw new RuntimeException("Permission denied");

    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        String compareUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User shouldReviewer = userRepository.findByUsername(compareUsername).orElseThrow(() -> new UsernameNotFoundException("User not found: " + compareUsername));
        if (shouldReviewer.getRole().equals(REVIEWER)) {
            return  userMapper.userToUserResponseDTO(user);
        }
        throw new RuntimeException("Permission denied");

    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        String compareUsername = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (user.getUsername().equals(compareUsername)) {
            userRepository.delete(user);
        }
        throw new RuntimeException("Permission denied");
    }

}
