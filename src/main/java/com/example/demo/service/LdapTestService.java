package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
public class LdapTestService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public void testConnection() {
        ldapTemplate.search(
                "",
                "(objectClass=*)",
                (AttributesMapper<String>) attrs -> attrs.get("cn") != null ? attrs.get("cn").get().toString() : "No CN"
        ).forEach(System.out::println);
    }


}
