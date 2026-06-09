package com.ldap.manager.service;

import com.ldap.manager.Dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

@Service
@Slf4j
public class UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * Creates a new LDAP user under ou=users
     */
    public UserDto createUser(UserDto request) {

        UserDto response = new UserDto();

        try {

            log.info(
                    "Creating LDAP user with uid [{}]",
                    request.getUid()
            );

            // Build User DN
            // Example:
            // uid=john,ou=users
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "users")
                    .add("uid", request.getUid())
                    .build();

            BasicAttributes attributes = new BasicAttributes();

            // LDAP Object Classes
            BasicAttribute objectClass =
                    new BasicAttribute("objectClass");

            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");

            attributes.put(objectClass);

            // Mandatory attributes
            attributes.put("uid", request.getUid());
            attributes.put(
                    "cn",
                    request.getFirstName() + " " +
                            request.getLastName()
            );
            attributes.put(
                    "sn",
                    request.getLastName()
            );
            attributes.put(
                    "givenName",
                    request.getFirstName()
            );

            // Optional attributes

            if (request.getEmail() != null
                    && !request.getEmail().isBlank()) {

                attributes.put(
                        "mail",
                        request.getEmail()
                );
            }

            if (request.getMobileNumber() != null
                    && !request.getMobileNumber().isBlank()) {

                attributes.put(
                        "mobile",
                        request.getMobileNumber()
                );
            }

            if (request.getDepartment() != null
                    && !request.getDepartment().isBlank()) {

                attributes.put(
                        "ou",
                        request.getDepartment()
                );
            }

            if (request.getDesignation() != null
                    && !request.getDesignation().isBlank()) {

                attributes.put(
                        "title",
                        request.getDesignation()
                );
            }

            // Password
            attributes.put(
                    "userPassword",
                    request.getPassword()
            );

            // Create LDAP Entry
            ldapTemplate.bind(
                    dn,
                    null,
                    attributes
            );

            log.info(
                    "LDAP user created successfully [{}]",
                    request.getUid()
            );

            response.setUid(request.getUid());
            response.setFirstName(
                    request.getFirstName() + " " +
                            request.getLastName()
            );
            response.setEmail(request.getEmail());
            response.setMobileNumber(
                    request.getMobileNumber()
            );
            response.setDepartment(
                    request.getDepartment()
            );
            response.setDesignation(
                    request.getDesignation()
            );

            return response;

        } catch (Exception ex) {

            log.error(
                    "Failed to create LDAP user [{}]",
                    request.getUid(),
                    ex
            );

            response.setUid(request.getUid());
            response.setFirstName(
                    request.getFirstName() + " " +
                            request.getLastName()
            );

            // You can add a message field in DTO if available
            // response.setMessage("Failed: " + ex.getMessage());

            return response;
        }
    }
}