package com.rukspot.sample.restclient;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

public class UserMgt {
    RemoteUserStoreManagerServiceStub stub;

    public boolean isUserExist(String user)
            throws RemoteException, RemoteUserStoreManagerServiceUserStoreExceptionException {
        return stub.isExistingUser(user);
    }

    public void addUser(String user, String pass, String[] roles)
            throws RemoteException, RemoteUserStoreManagerServiceUserStoreExceptionException {
        boolean isExist = stub.isExistingUser(user);
        if (isExist) {
//            System.out.println("user " + user + " Already exist");
        } else {
            stub.addUser(user, pass, roles, null, null, false);
        }
    }

    public UserMgt(String user, String pass, String endpoint) {
        System.setProperty("javax.net.ssl.keyStore",
                Settings.AM_HOME + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStore",
                Settings.AM_HOME + "/repository/resources/security/client-truststore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");
        try {
            stub = new RemoteUserStoreManagerServiceStub(endpoint + "/services/RemoteUserStoreManagerService");
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        ServiceClient client = stub._getServiceClient();
        Options client_options = client.getOptions();
        Authenticator authenticator = new Authenticator();
        authenticator.setUsername(user);
        authenticator.setPassword(pass);
        authenticator.setPreemptiveAuthentication(true);
        client_options.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, authenticator);
        client.setOptions(client_options);
    }

    public void populateUsers(String[] users) throws Exception {
        for (String user : users) {
            if (!isUserExist(user)) {
//                addUser(user, "admin");
            }
        }
    }
}
