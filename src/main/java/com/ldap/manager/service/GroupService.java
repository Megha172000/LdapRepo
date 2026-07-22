package com.ldap.manager.service;

import com.ldap.manager.Dto.AddUserToGroupRequest;
import com.ldap.manager.Dto.CreateGroupRequestDto;
import com.ldap.manager.Dto.GroupDto;
import com.ldap.manager.Dto.GroupResponseDto;
import com.ldap.manager.Dto.PendingRequestDto;
import com.ldap.manager.entity.GroupAccessRequest;
import com.ldap.manager.repo.GroupAccessRequestRepository;
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
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for LDAP Group management operations as well as
 * administrative approval workflows for group access requests.
 */
@Service
@Slf4j
public class GroupService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private GroupAccessRequestRepository groupAccessRequestRepository;

    /**
     * Creates a new LDAP group entry under the 'ou=groups' Organizational Unit.
     *
     * @param request Payload containing group details such as name and description.
     * @return GroupDto containing execution confirmation and group details.
     */
    public GroupDto createGroup(CreateGroupRequestDto request) {
        log.info("Initiating creation of LDAP group: [{}]", request.getGroupName());

        try {
            // Build the Distinguished Name (DN) -> cn=<groupName>,ou=groups
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "groups")
                    .add("cn", request.getGroupName())
                    .build();

            BasicAttributes attributes = new BasicAttributes();

            // Set mandatory LDAP object classes for group entries
            BasicAttribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("groupOfNames");
            attributes.put(objectClass);

            // Set Common Name (CN)
            attributes.put("cn", request.getGroupName());

            // 'groupOfNames' requires at least one initial member upon creation
            attributes.put("member", "uid=testuser,ou=users,dc=example,dc=com");

            // Attach optional group description if provided
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                attributes.put("description", request.getDescription());
            }

            // Bind entry to the LDAP directory
            ldapTemplate.bind(dn, null, attributes);

            log.info("LDAP group [{}] created successfully", request.getGroupName());

            GroupDto response = new GroupDto();
            response.setGroupName(request.getGroupName());
            response.setDescription(request.getDescription());
            response.setMessage("LDAP group created successfully");

            return response;

        } catch (Exception ex) {
            log.error("Failed to create LDAP group [{}]", request.getGroupName(), ex);
            throw new RuntimeException("Unable to create LDAP group: " + ex.getMessage(), ex);
        }
    }

    /**
     * Directly adds a user into an existing LDAP group's member attribute.
     *
     * @param request Payload containing target username and group name.
     * @return GroupResponseDto confirming successful member addition.
     */
    public GroupResponseDto addUserToGroup(AddUserToGroupRequest request) {
        log.info("Adding user [{}] to LDAP group [{}]", request.getUsername(), request.getGroupName());

        try {
            // Construct target Group DN and User DN strings
            String groupDn = "cn=" + request.getGroupName() + ",ou=groups";
            String userDn = "uid=" + request.getUsername() + ",ou=users";

            // Prepare modification item to add the member attribute
            ModificationItem[] modifications = new ModificationItem[1];
            Attribute member = new BasicAttribute("member", userDn);
            modifications[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, member);

            // Execute modification against LDAP directory
            ldapTemplate.modifyAttributes(groupDn, modifications);

            log.info("User [{}] successfully added to LDAP group [{}]", request.getUsername(), request.getGroupName());

            GroupResponseDto response = new GroupResponseDto();
            response.setUsername(request.getUsername());
            response.setGroupName(request.getGroupName());
            response.setMessage("User added to LDAP group successfully");

            return response;

        } catch (Exception ex) {
            log.error("Failed to add user [{}] to LDAP group [{}]", request.getUsername(), request.getGroupName(), ex);
            throw new RuntimeException("Unable to add user to group: " + ex.getMessage(), ex);
        }
    }

    /**
     * Submits a pending access request for admin approval rather than adding directly.
     *
     * @param request Payload containing username and desired group name.
     * @return Confirmation message indicating request submission.
     */
    public String requestAddUserToGroup(AddUserToGroupRequest request) {
        log.info("Submitting group access request for user [{}] to join group [{}]",
                request.getUsername(), request.getGroupName());

        GroupAccessRequest accessRequest = new GroupAccessRequest();
        accessRequest.setUsername(request.getUsername());
        accessRequest.setGroupName(request.getGroupName());
        accessRequest.setStatus("PENDING");

        GroupAccessRequest savedRequest = groupAccessRequestRepository.save(accessRequest);
        log.info("Access request saved with ID [{}] and status [PENDING]", savedRequest.getId());

        return "Group access request sent to admin";
    }

    /**
     * Approves a pending group access request by granting LDAP group membership
     * and updating request status in the database.
     *
     * @param id Primary key ID of the GroupAccessRequest entity.
     */
    public void approveRequest(Long id) {
        log.info("Processing approval for group access request ID [{}]", id);

        GroupAccessRequest request = groupAccessRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Approval failed: Group access request ID [{}] not found", id);
                    return new RuntimeException("Request not found with ID: " + id);
                });

        // Delegate to direct LDAP addition
        AddUserToGroupRequest ldapRequest = new AddUserToGroupRequest();
        ldapRequest.setUsername(request.getUsername());
        ldapRequest.setGroupName(request.getGroupName());

        addUserToGroup(ldapRequest);

        // Update database record status
        request.setStatus("APPROVED");
        groupAccessRequestRepository.save(request);

        log.info("Request ID [{}] approved and user [{}] added to group [{}]",
                id, request.getUsername(), request.getGroupName());
    }

    /**
     * Retrieves all LDAP groups matching the 'groupOfNames' object class.
     *
     * @return List of GroupDto representing available LDAP groups.
     */
    public List<GroupDto> getAllGroups() {
        log.info("Searching for all LDAP groups under 'ou=groups'");

        List<GroupDto> groups = ldapTemplate.search(
                "ou=groups",
                "(objectClass=groupOfNames)",
                (AttributesMapper<GroupDto>) attributes -> {
                    GroupDto group = new GroupDto();
                    group.setGroupName(getAttribute(attributes, "cn"));
                    group.setDescription(getAttribute(attributes, "description"));
                    return group;
                }
        );

        log.info("Successfully fetched [{}] LDAP groups", groups.size());
        return groups;
    }

    /**
     * Retrieves all pending group access requests awaiting administrator review.
     *
     * @return List of PendingRequestDto records with 'PENDING' status.
     */
    public List<PendingRequestDto> getPendingRequests() {
        log.info("Fetching all pending group access requests from database");

        List<GroupAccessRequest> requests = groupAccessRequestRepository.findByStatus("PENDING");
        List<PendingRequestDto> response = new ArrayList<>();

        for (GroupAccessRequest request : requests) {
            PendingRequestDto dto = new PendingRequestDto();
            dto.setId(request.getId());
            dto.setUsername(request.getUsername());
            dto.setGroupName(request.getGroupName());
            dto.setStatus(request.getStatus());
            dto.setCreatedAt(request.getCreatedAt());

            response.add(dto);
        }

        log.info("Retrieved [{}] pending access requests", response.size());
        return response;
    }

    /**
     * Helper method to safely retrieve single string attributes from LDAP entries.
     *
     * @param attributes    The LDAP attributes map.
     * @param attributeName Target attribute key name.
     * @return Attribute value string, or null if key does not exist or fails.
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