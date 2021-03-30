package org.openziti.ldap;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openziti.ZitiContext;

public class ZitiLdapConnectionConfigTest {

    private ZitiLdapConnection zitiLdapConnection;

    @Test
    public void testZitiLdapConnectionConfig() {
        ZitiLdapConnectionConfig zitiLdapConnectionConfig = new ZitiLdapConnectionConfig.Builder().service("adldapsvc").bindDn("test").bindPass("testpass").build();
        ZitiContext zitiContext = Mockito.mock(ZitiContext.class);
        zitiLdapConnection = new ZitiLdapConnection(zitiContext,zitiLdapConnectionConfig);
        Assertions.assertNotNull(zitiLdapConnection);
    }
}
