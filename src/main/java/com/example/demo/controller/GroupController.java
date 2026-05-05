package com.example.demo.controller;

import com.example.demo.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")

public class GroupController {

    @Autowired
    private LdapService ldapService;

    @PostMapping("/add-user")
    public String addUserToGroup(
            @RequestParam String username,
            @RequestParam String groupName) {

        ldapService.addUserToGroup(username, groupName);
        return "User added to group successfully";
    }
}
