package com.ldap.manager.repo;

import com.ldap.manager.entity.GroupAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupAccessRequestRepository
        extends JpaRepository<GroupAccessRequest,Long> {


}