package com.ldap.manager.repo;

import com.ldap.manager.entity.GroupAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupAccessRequestRepository
        extends JpaRepository<GroupAccessRequest,Long> {

    List<GroupAccessRequest> findByStatus(String status);

}