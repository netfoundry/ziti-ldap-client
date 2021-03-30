package org.openziti.ldap;

/**
 * Class that encapsulates parameters required to access ldap in ziti network
 */
public class ZitiLdapConnectionConfig {

    /** name of the ziti service providing ldap service */
    private String serviceName;

    /** ldap bind dn */
    private String bindDn;

    /** ldap bind dn password */
    private String bindPass;

    /**
     * private constructor called by the builder class method
     * @param serviceName name of the ziti service providing ldap service
     * @param bindDn ldap bind dn
     * @param bindPass ldap bind dn password
     */
    private ZitiLdapConnectionConfig(String serviceName,String bindDn,String bindPass){
        this.serviceName = serviceName;
        this.bindDn = bindDn;
        this.bindPass = bindPass;
    }

    /** @return ziti service name */
    public String getServiceName() {
        return serviceName;
    }

    /** @return ldap bind dn */
    public String getBindDn() {
        return bindDn;
    }

    /** @return ldap bind pass */
    public String getBindPass() {
        return bindPass;
    }

    /** Class that builds the ldap connection configuration object */
    public static class Builder {

        /** name of the ziti service providing ldap service */
        private String serviceName;

        /** ldap bind dn */
        private String bindDn;

        /** ldap bind dn password */
        private String bindPass;

        /** setter ziti service name */
        public Builder service(String serviceName){
            this.serviceName = serviceName;
            return this;
        }

        /** setter ldap bind db */
        public Builder bindDn(String bindDn){
            this.bindDn  = bindDn;
            return this;
        }

        /** setter ldap bind password */
        public Builder bindPass(String bindPass){
            this.bindPass = bindPass;
            return this;
        }

        /** @return ziti based ldap connection configuration */
        public ZitiLdapConnectionConfig build(){
            return new ZitiLdapConnectionConfig(this.serviceName,this.bindDn,this.bindPass);
        }
    }

}
