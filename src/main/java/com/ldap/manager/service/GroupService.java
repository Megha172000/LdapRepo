package com.ldap.manager.service;

import com.ldap.manager.Dto.AddUserToGroupRequest;
import com.ldap.manager.Dto.CreateGroupRequestDto;
import com.ldap.manager.Dto.GroupDto;
import com.ldap.manager.Dto.GroupResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.*;

@Service
@Slf4j
public class GroupService {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * Creates a new LDAP group under ou=groups
     */
    public GroupDto createGroup(CreateGroupRequestDto request) {

        try {

            log.info("Creating LDAP group: {}", request.getGroupName());

            // Build DN
            // Result:
            // cn=developers,ou=groups
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "groups")
                    .add("cn", request.getGroupName())
                    .build();

            BasicAttributes attributes = new BasicAttributes();

            // Object classes required for LDAP group
            BasicAttribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("groupOfNames");

            attributes.put(objectClass);

            // Group name
            attributes.put("cn", request.getGroupName());

            // groupOfNames requires at least one member
            attributes.put(
                    "member",
                    "uid=testuser,ou=users,dc=example,dc=com"
            );

            // Optional description
            if (request.getDescription() != null &&
                    !request.getDescription().isBlank()) {

                attributes.put(
                        "description",
                        request.getDescription()
                );
            }

            // Create LDAP entry
            ldapTemplate.bind(dn, null, attributes);

            log.info("LDAP group created successfully: {}",
                    request.getGroupName());

            GroupDto response = new GroupDto();
            response.setGroupName(request.getGroupName());
            response.setDescription(request.getDescription());
            response.setMessage("LDAP group created successfully");

            return response;

        } catch (Exception ex) {

            log.error(
                    "Failed to create LDAP group [{}]",
                    request.getGroupName(),
                    ex
            );

            throw new RuntimeException(
                    "Unable to create LDAP group: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Adds a user DN into group's member attribute
     */
    public GroupResponseDto addUserToGroup(
            AddUserToGroupRequest request
    ) {

        try {

            log.info(
                    "Adding user [{}] to group [{}]",
                    request.getUsername(),
                    request.getGroupName()
            );

            // Group DN
            String groupDn =
                    "cn=" + request.getGroupName()
                            + ",ou=groups";

            // User DN
            String userDn =
                    "uid=" + request.getUsername()
                            + ",ou=users";

            ModificationItem[] modifications =
                    new ModificationItem[1];

            // Add member attribute
            Attribute member =
                    new BasicAttribute(
                            "member",
                            userDn
                    );

            modifications[0] =
                    new ModificationItem(
                            DirContext.ADD_ATTRIBUTE,
                            member
                    );

            ldapTemplate.modifyAttributes(
                    groupDn,
                    modifications
            );

            log.info(
                    "User [{}] successfully added to group [{}]",
                    request.getUsername(),
                    request.getGroupName()
            );

            GroupResponseDto response =
                    new GroupResponseDto();

            response.setUsername(
                    request.getUsername()
            );
            response.setGroupName(
                    request.getGroupName()
            );
            response.setMessage(
                    "User added to LDAP group successfully"
            );

            return response;

        } catch (Exception ex) {

            log.error(
                    "Failed to add user [{}] to group [{}]",
                    request.getUsername(),
                    request.getGroupName(),
                    ex
            );

            throw new RuntimeException(
                    "Unable to add user to group: "
                            + ex.getMessage(),
                    ex
            );
        }
    }
}