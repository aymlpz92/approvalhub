package com.approvalhub.controller;

import com.approvalhub.dto.user.UserResponseDTO;
import com.approvalhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDTO> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public UserResponseDTO findByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }



    @DeleteMapping("/{username}")
    public void deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

}
