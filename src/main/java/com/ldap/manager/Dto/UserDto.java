package com.ldap.manager.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDto{

    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String department;
    private String designation;
}
