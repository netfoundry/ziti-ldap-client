package org.openziti;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapException;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.openziti.ldap.ZitiLdapConnection;
import org.openziti.ldap.ZitiLdapConnectionConfig;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Unit test for Ziti App
 */
public class ZitiAppTest {

    static Logger logger;
    @BeforeAll
    public static void setUp() {
        logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.TRACE);
    }


    public void tstGetContext() {
        String credJson = "{\"ztAPI\":\"https://10.0.0.1:443\",\"id\":{\"key\":\"pem:-----BEGIN EC PRIVATE KEY-----\\nMIGvJiQISo=\\n-----END EC PRIVATE KEY-----\\n\",\"cert\":\"pem:-----BEGIN CERTIFICATE-----\\nMIID3jZEFQ==\\n-----END CERTIFICATE-----\\n\",\"ca\":\"pem:-----BEGIN CERTIFICATE-----\\nMIIFvNd+oI=\\n-----END CERTIFICATE-----\\n\"},\"configTypes\":null}";
        try {
            ZitiApp zitiApp = new ZitiApp.CredentialBuilder().fromJson(credJson).build();
            Assertions.assertNotNull(zitiApp);
            Assertions.fail("Not expected to pass");
        }catch(Exception e){
            Assertions.assertTrue(e.getMessage().contains("unsupported format"));
        }
    }

    public void tstEnroll() throws LdapException {

        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbSI6Im90dCIsImV4cCI6MTYxNTcwNzgyOCwiaXNzIjoiaHR0cHM6Ly8yMy4yMi4xMjcuMTI1OjQ0MyIsImp0aSI6IjU4NGFjYjczLWM3OWQtNDcxOC1iNTg3LTY1MTMzODllNTUzYiIsInN1YiI6Ik1wT0lOeEdCQyJ9.P_2LZR21iRAyh0FM992Jh0oqWKwLmj2YILidLDc7je5zFvbvwHhIaShCnDsj2NW1RUA6rV5fW-RMzDebeAbqeC6Ff0P1DMJkK1M8jUaX3Ggcu2nvSzNi5CoA0v1ggR_WHY_E1-yrDxBGfdG31nmVRRdi9CL8yWkK10PfgUYA-AklvgA_aPNPWlyTLFpSLGq-kQ2bWE_kn7u51dKCht8WCatn4UEWf2W8-MhroclSXGhdG0NCTe8H3KWVPrSCvz1mxkIoUVQzn3V1mLrqGzkmbKJucnxj6eCoBFRTJ0CE4UW27dCGQ5w1ncnCB2FsSsBR89ASO242EPhvSfoTb4itPg";

        InputStream tokenStream = new ByteArrayInputStream(token.getBytes());

        String identity = ZitiApp.enroll(tokenStream);

        logger.info("ziti identity : {}",identity);

        ZitiContext zitiContext = new ZitiApp.CredentialBuilder().fromKey(identity).build().getContext();

        ZitiLdapConnectionConfig zitiLdapConnectionConfig = new ZitiLdapConnectionConfig.Builder().service("ad ldap tcp - ad.sandbox.internal").bindDn("sandbox\\xxx").bindPass("xxxxx").build();

        ZitiLdapConnection zitiLdapConnection = new ZitiLdapConnection(zitiContext,zitiLdapConnectionConfig);

        zitiLdapConnection.open();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setBaseDn("OU=sandbox,DC=ad,DC=sandbox,DC=netfoundry,DC=io");
        searchRequest.setFilter("(&(objectClass=user))");
        searchRequest.setReturnAttributes("sn","givenName", "samAccountName");

        org.ldaptive.SearchOperationHandle searchOperationHandle = zitiLdapConnection.operation(searchRequest);

        SearchResponse searchResponse = searchOperationHandle.execute();

        logger.info("Search response status : {}",searchResponse.isSuccess());

        searchResponse.getEntries().forEach(ldapEntry -> ldapEntry.getAttributes().forEach(ldapAttribute -> logger.info("Attribute Name : {}  Attribute Value : {}",ldapAttribute.getName(),ldapAttribute.getStringValue())));

        zitiLdapConnection.close();

    }

}
