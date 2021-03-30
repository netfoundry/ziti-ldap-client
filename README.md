# ziti-ldap-client - V1.0.0
LDAP client to connect and operate on directory servers protected by a ziti network



# 1. Add dependency in your application
*********************************************************************
```html
<dependency>
    <groupId>org.openziti</groupId>
    <artifactId>ziti-ldap-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

# 2. Ziti Context Initialization 
*********************************************************************
a) Use the JWT token and ziti tunneler to enroll  and create ziti credentials json file    
```html
ziti-tunnel enroll --jwt ZitifiedAD.jwt
```

b) Store the generated credentials json in your application's secret vault and load the same into your applicaiton's environment
```html
String credJson = "{\"ztAPI\":\"https://10.0.0.1:443\",\"id\":{\"key\":\"pem:-----BEGIN EC PRIVATE KEY-----\\nMIGvJiQISo=\\n-----END EC PRIVATE KEY-----\\n\",\"cert\":\"pem:-----BEGIN CERTIFICATE-----\\nMIID3jZEFQ==\\n-----END CERTIFICATE-----\\n\",\"ca\":\"pem:-----BEGIN CERTIFICATE-----\\nMIIFvNd+oI=\\n-----END CERTIFICATE-----\\n\"},\"configTypes\":null}";
```

c) Create Ziti Context
```html
ZitiContext zitiContext = new ZitiApp.CredentialBuilder().fromJson(credJson).build().getContext();
```

# 3. Initialize and open LDAP connection
*********************************************************************
```html
ZitiLdapConnectionConfig zitiLdapConnectionConfig = new ZitiLdapConnectionConfig.Builder().service("ad ldap tcp - ad.sandbox.internal").bindDn("sandbox\\xxxx").bindPass("xxxxx").build();

ZitiLdapConnection zitiLdapConnection = new ZitiLdapConnection(zitiContext,zitiLdapConnectionConfig);

zitiLdapConnection.open();
```

# 4. Search and filter LDAP Users
*********************************************************************
```html
SearchRequest searchRequest = new SearchRequest();
searchRequest.setBaseDn("OU=sandbox,DC=ad,DC=sandbox,DC=netfoundry,DC=io");
searchRequest.setFilter("(&(objectClass=user))");
searchRequest.setReturnAttributes("sn","givenName", "samAccountName");

org.ldaptive.SearchOperationHandle searchOperationHandle = zitiLdapConnection.operation(searchRequest);
SearchResponse searchResponse = searchOperationHandle.execute();
log.info("Search response status : {}",searchResponse.isSuccess());

searchResponse.getEntries().stream().forEach(ldapEntry -> ldapEntry.getAttributes().stream().forEach(ldapAttribute -> {
    log.info("Attribute Name : {}  Attributte Value : {}",ldapAttribute.getName(),ldapAttribute.getStringValue());
}));

```

# 5. Close LDAP connection
*********************************************************************
```html
zitiLdapConnection.close();
```


# ziti-ldap-client - V1.1.0
Provides support to enroll endpoint using one time jwt token and create the ziti context from the enrolled id.


# 1. Add dependency in your application
*********************************************************************
```html
<dependency>
    <groupId>org.openziti</groupId>
    <artifactId>ziti-ldap-client</artifactId>
    <version>1.1.0</version>
</dependency>
```

# 2. Ziti Context Initialization
*********************************************************************
a) Enroll the endpoint using the one time jwt enrollment token file downloaded from nfconsole. The enroll method produces a ziti identity as a base64 encoded string containing keystore file
```html
String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbSI6Im90dCIsImV4cCI6MTYxNTcwNzgyOCwiaXNzIjoiaHR0cHM6Ly8yMy4yMi4xMjcuMTI1OjQ0MyIsImp0aSI6IjU4NGFjYjczLWM3OWQtNDcxOC1iNTg3LTY1MTMzODllNTUzYiIsInN1YiI6Ik1wT0lOeEdCQyJ9.P_2LZR21iRAyh0FM992Jh0oqWKwLmj2YILidLDc7je5zFvbvwHhIaShCnDsj2NW1RUA6rV5fW-RMzDebeAbqeC6Ff0P1DMJkK1M8jUaX3Ggcu2nvSzNi5CoA0v1ggR_WHY_E1-yrDxBGfdG31nmVRRdi9CL8yWkK10PfgUYA-AklvgA_aPNPWlyTLFpSLGq-kQ2bWE_kn7u51dKCht8WCatn4UEWf2W8-MhroclSXGhdG0NCTe8H3KWVPrSCvz1mxkIoUVQzn3V1mLrqGzkmbKJucnxj6eCoBFRTJ0CE4UW27dCGQ5w1ncnCB2FsSsBR89ASO242EPhvSfoTb4itPg";

InputStream tokenStream = new ByteArrayInputStream(token.getBytes());

String identity = ZitiApp.enroll(tokenStream);
```

b) Store the generated  base64 encoded keystore file in your application's secret vault and load the same into your applicaiton's environment
```html
String identity = "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";
```

c) Create Ziti Context
```html
ZitiContext zitiContext = new ZitiApp.CredentialBuilder().fromKey(identity).build().getContext();
```