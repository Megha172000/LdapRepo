package com.ldap.manager.controller;

import com.ldap.manager.Dto.GroupDto;
import com.ldap.manager.Dto.UserDto;
import com.ldap.manager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    public UserDto addUser(
            @RequestBody UserDto request
    ) {

        log.info(
                "Received request to create user [{}]",
                request.getUid()
        );

        return userService.createUser(request);
    }

    @GetMapping("/get-all-users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }


}