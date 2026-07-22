package com.ldap.manager.service;

import com.ldap.manager.Dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.List;

/**
 * Service responsible for managing LDAP User lifecycle operations,
 * including user provisioning and directory searches under 'ou=users'.
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * Creates a new LDAP user entry under the 'ou=users' Organizational Unit.
     *
     * @param request Payload containing user profile details (UID, Names, Credentials, Contact, Org unit).
     * @return UserDto containing the created user attributes.
     */
    public UserDto createUser(UserDto request) {
        log.info("Initiating creation of LDAP user with UID: [{}]", request.getUid());

        try {
            // Build Distinguished Name (DN) -> uid=<uid>,ou=users
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "users")
                    .add("uid", request.getUid())
                    .build();

            BasicAttributes attributes = new BasicAttributes();

            // Object classes required for an inetOrgPerson entry
            BasicAttribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");
            attributes.put(objectClass);

            // Mandatory LDAP attributes
            attributes.put("uid", request.getUid());
            attributes.put("cn", request.getFirstName() + " " + request.getLastName());
            attributes.put("sn", request.getLastName());
            attributes.put("givenName", request.getFirstName());

            // Optional LDAP attributes mapping
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                attributes.put("mail", request.getEmail());
            }

            if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
                attributes.put("mobile", request.getMobileNumber());
            }

            if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
                attributes.put("ou", request.getDepartment());
            }

            if (request.getDesignation() != null && !request.getDesignation().isBlank()) {
                attributes.put("title", request.getDesignation());
            }

            // User Password attribute
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                attributes.put("userPassword", request.getPassword());
            }

            // Create entry in LDAP directory
            ldapTemplate.bind(dn, null, attributes);

            log.info("LDAP user [{}] created successfully", request.getUid());

            UserDto response = new UserDto();
            response.setUid(request.getUid());
            response.setFirstName(request.getFirstName());
            response.setLastName(request.getLastName());
            response.setEmail(request.getEmail());
            response.setMobileNumber(request.getMobileNumber());
            response.setDepartment(request.getDepartment());
            response.setDesignation(request.getDesignation());

            return response;

        } catch (Exception ex) {
            log.error("Failed to create LDAP user with UID [{}]", request.getUid(), ex);
            throw new RuntimeException("Unable to create LDAP user: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves all LDAP users belonging to the 'inetOrgPerson' object class.
     *
     * @return List of UserDto containing mapped user profiles.
     */
    public List<UserDto> getAllUsers() {
        log.info("Searching for all LDAP users under 'ou=users'");

        List<UserDto> users = ldapTemplate.search(
                "ou=users",
                "(objectClass=inetOrgPerson)",
                (AttributesMapper<UserDto>) attributes -> {
                    UserDto user = new UserDto();
                    user.setUid(getAttribute(attributes, "uid"));
                    user.setFirstName(getAttribute(attributes, "givenName"));
                    user.setLastName(getAttribute(attributes, "sn"));
                    user.setEmail(getAttribute(attributes, "mail"));
                    user.setMobileNumber(getAttribute(attributes, "mobile"));
                    user.setDepartment(getAttribute(attributes, "ou"));
                    user.setDesignation(getAttribute(attributes, "title"));
                    return user;
                }
        );

        log.info("Successfully fetched [{}] LDAP users", users.size());
        return users;
    }

    /**
     * Safely retrieves a single string attribute value from an LDAP Attributes set.
     *
     * @param attributes    The LDAP attributes object.
     * @param attributeName Target attribute key name.
     * @return String value of the attribute, or null if missing or invalid.
     */
    private String getAttribute(Attributes attributes, String attributeName) {
        try {
            Attribute attribute = attributes.get(attributeName);
            return (attribute != null) ? attribute.get().toString() : null;
        } catch (Exception ex) {
            log.warn("Failed to read attribute [{}] from LDAP attributes", attributeName, ex);
            return null;
        }
    }
}