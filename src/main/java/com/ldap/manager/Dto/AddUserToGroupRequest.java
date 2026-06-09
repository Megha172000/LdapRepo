package com.ldap.manager.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AddUserToGroupRequest {
    private String username;
    private String groupName;
}
