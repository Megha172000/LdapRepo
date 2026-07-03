package com.ldap.manager.controller;

import com.ldap.manager.Dto.AddUserToGroupRequest;
import com.ldap.manager.Dto.CreateGroupRequestDto;
import com.ldap.manager.Dto.GroupDto;
import com.ldap.manager.Dto.GroupResponseDto;
import com.ldap.manager.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@Slf4j
public class GroupController {

    @Autowired
    private GroupService groupService;


    /**
     * Create a new LDAP group
     */
    @PostMapping("/create-group")
    public GroupDto addGroup(
            @RequestBody CreateGroupRequestDto request
    ) {

        log.info("Received request to create group [{}]",
                request.getGroupName());

        return groupService.createGroup(request);
    }


    /**
     * Add existing LDAP user to an LDAP group
     */
   /* @PostMapping("/add-user-to-group")
    public GroupResponseDto addUserToGroup(
            @RequestBody AddUserToGroupRequest request
    ) {

        log.info("Received request to add user [{}] to group [{}]",
                request.getUsername(),
                request.getGroupName());

        return groupService.addUserToGroup(request);
    }*/

    @PostMapping("/add-user-to-group")
    public String addUserToGroup(
            @RequestBody AddUserToGroupRequest request
    ) {

        log.info(
                "User [{}] requested access to group [{}]",
                request.getUsername(),
                request.getGroupName()
        );


        return groupService.requestAddUserToGroup(request);
    }


    @PutMapping("/approve/{id}")
    public String approveRequest(
            @PathVariable Long id
    ){

        groupService.approveRequest(id);


        return "User added to group";

    }
}