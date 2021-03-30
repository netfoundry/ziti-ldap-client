package org.openziti.ldap;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.ldaptive.*;
import org.ldaptive.control.RequestControl;
import org.ldaptive.extended.ExtendedOperationHandle;
import org.ldaptive.extended.ExtendedRequest;
import org.ldaptive.sasl.DefaultSaslClientRequest;
import org.ldaptive.sasl.SaslClientRequest;
import org.openziti.ZitiContext;
import org.openziti.netty.ZitiChannelFactory;
import org.slf4j.LoggerFactory;

/**
 * Ziti Ldap Connection Implementation
 */
public class ZitiLdapConnection implements Connection
{
    /** Logger for this class. */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ZitiLdapConnection.class);

    /** Required valid ldap URI, though not required to be accessible on a ziti network */
    public static final String URL = "ldap://ztaccess";

    /** Netty based ldaptive connection customized to access ldap protected by ziti network */
    private final ZitiNettyConnection ztNettyConnection;

    /**
     *
     * @param zitiContext created with a valid ziti identity
     * @param zitiLdapConnectionConfig parameters required to access ldap in ziti network
     */
    public ZitiLdapConnection(ZitiContext zitiContext, ZitiLdapConnectionConfig zitiLdapConnectionConfig){

        org.ldaptive.ConnectionConfig connectionConfig = org.ldaptive.ConnectionConfig.builder()
                .url(URL)
                .useStartTLS(false)
                .connectionInitializers(BindConnectionInitializer.builder()
                        .dn(zitiLdapConnectionConfig.getBindDn())
                        .credential(new Credential(zitiLdapConnectionConfig.getBindPass()))
                        .build())
                .build();

        this.ztNettyConnection = new ZitiNettyConnection(connectionConfig, new ZitiChannelFactory(zitiContext),zitiLdapConnectionConfig.getServiceName(), new NioEventLoopGroup(), new DefaultEventLoopGroup(), false);

        log.info("Ldap connection using ziti transport initialized");
    }

    /**
     * Abandon request
     * @param abandonRequest request id
     */
    @Override
    public void operation(AbandonRequest abandonRequest) {
        ztNettyConnection.operation(abandonRequest);
    }

    /**
     * Create ldap entries
     * @param addRequest values
     * @return handle to access operation response
     */
    @Override
    public OperationHandle<AddRequest, AddResponse> operation(AddRequest addRequest) {
        return ztNettyConnection.operation(addRequest);
    }

    /**
     * Ldap simple bind request
     * @param bindRequest values
     * @return handle to access operation response
     */
    @Override
    public OperationHandle<BindRequest, BindResponse> operation(BindRequest bindRequest) {
        return ztNettyConnection.operation(bindRequest);
    }

    /**
     * Compare ldap entries
     * @param compareRequest values
     * @return handle to access operation response
     */
    @Override
    public CompareOperationHandle operation(CompareRequest compareRequest) {
        return ztNettyConnection.operation(compareRequest);
    }

    /**
     * Delete ldap entry
     * @param deleteRequest values
     * @return handle to access operation response
     */
    @Override
    public OperationHandle<DeleteRequest, DeleteResponse> operation(DeleteRequest deleteRequest) {
        return ztNettyConnection.operation(deleteRequest);
    }

    /**
     * @param extendedRequest values
     * @return handle to access operation response
     */
    @Override
    public ExtendedOperationHandle operation(ExtendedRequest extendedRequest) {
        return ztNettyConnection.operation(extendedRequest);
    }

    /**
     * Modify ldap entry
     * @param modifyRequest values
     * @return handle to access operation response
     */
    @Override
    public OperationHandle<ModifyRequest, ModifyResponse> operation(ModifyRequest modifyRequest) {
        return ztNettyConnection.operation(modifyRequest);
    }

    /**
     * Modify DN
     * @param modifyDnRequest values
     * @return handle to access operation response
     */
    @Override
    public OperationHandle<ModifyDnRequest, ModifyDnResponse> operation(ModifyDnRequest modifyDnRequest) {
        return ztNettyConnection.operation(modifyDnRequest);
    }

    /**
     * Ldap search request
     * @param searchRequest values
     * @return handle to access operation response
     */
    @Override
    public SearchOperationHandle operation(SearchRequest searchRequest) {
        return ztNettyConnection.operation(searchRequest);
    }

    /**
     * Bind to ldap using SASL.
     * @param saslClientRequest attributes
     * @return bind response
     * @throws LdapException on bind failure
     */
    @Override
    public BindResponse operation(SaslClientRequest saslClientRequest) throws LdapException {
        return ztNettyConnection.operation(saslClientRequest);
    }

    /**
     * Bind to ldap using default SASL client
     * @param defaultSaslClientRequest attributes
     * @return bind response
     * @throws LdapException on bind failure
     */
    @Override
    public BindResponse operation(DefaultSaslClientRequest defaultSaslClientRequest) throws LdapException {
        return ztNettyConnection.operation(defaultSaslClientRequest);
    }

    /**
     * Not a required method in ziti based ldap connection
     * @return ldap url
     */
    @Override
    public LdapURL getLdapURL() {
        return ztNettyConnection.getLdapURL();
    }

    /**
     * Check if connection is open
     * @return boolean indicating status of ldap connection
     */
    @Override
    public boolean isOpen() {
        return ztNettyConnection.isOpen();
    }

    /**
     * Open ldap netty channel with ziti enabled transport
     * @throws LdapException on connection open failure
     */
    @Override
    public void open() throws LdapException {
        ztNettyConnection.open();
        log.info("Ldap connection using ziti transport opened");
    }

    /**
     * Close ldap netty channel
     */
    @Override
    public void close() {
        ztNettyConnection.close();
        log.info("Ldap connection using ziti transport closed");
    }

    @Override
    public void close(RequestControl... requestControls) {
        ztNettyConnection.close(requestControls);
    }
}