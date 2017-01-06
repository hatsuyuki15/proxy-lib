package org.hatsuyuki.proxy;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Hatsuyuki on 1/6/2017.
 */
class ProxyAuthenticator extends Authenticator {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static ProxyAuthenticator instance = new ProxyAuthenticator();
    private Table<String, Integer, PasswordAuthentication> authenticationTable = HashBasedTable.create();

    private ProxyAuthenticator() {
        Authenticator.setDefault(this);
    }

    public void addAuthentication(String host, int port, String username, String password) {
        authenticationTable.put(host, port, new PasswordAuthentication(username, password.toCharArray()));
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        PasswordAuthentication authentication = authenticationTable.get(getRequestingHost(), getRequestingPort());
        if (authentication == null) {
            LOGGER.error("No authentication info exists for " + getRequestingHost() + ":" + getRequestingPort());
            return null;
        } else {
            return authentication;
        }
    }

    public static ProxyAuthenticator getInstance() {
        return instance;
    }

}
