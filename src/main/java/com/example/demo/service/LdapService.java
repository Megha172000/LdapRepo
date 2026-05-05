package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public void addUserToGroup(String username, String groupName) {

        String groupDn = "cn=" + groupName + ",ou=groups";
        String userDn = "uid=" + username + ",ou=users,dc=example,dc=com"; // ✅ FIX

        ModificationItem[] mods = new ModificationItem[1];

        Attribute attr = new BasicAttribute("member", userDn);
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);

        ldapTemplate.modifyAttributes(groupDn, mods);
    }
}