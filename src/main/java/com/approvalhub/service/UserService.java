package com.approvalhub.service;

import com.approvalhub.domain.entity.User;
import com.approvalhub.dto.user.UserResponseDTO;
import com.approvalhub.mapper.UserMapper;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return  userMapper.userToUserResponseDTO(user);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        userRepository.delete(user);
    }

}
