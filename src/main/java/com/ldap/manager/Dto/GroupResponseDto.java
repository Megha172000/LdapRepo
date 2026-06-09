package com.ldap.manager.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDto {
    private String username;
    private String groupName;
    private String message;
}
