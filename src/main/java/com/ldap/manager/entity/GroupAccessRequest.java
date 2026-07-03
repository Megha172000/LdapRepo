package com.ldap.manager.entity;


import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "group_access_request")
public class GroupAccessRequest{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String groupName;
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();

}