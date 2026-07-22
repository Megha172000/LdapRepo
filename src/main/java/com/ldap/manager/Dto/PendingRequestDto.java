package com.ldap.manager.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PendingRequestDto {

    private Long id;

    private String username;

    private String groupName;

    private String status;

    private LocalDateTime createdAt;

}