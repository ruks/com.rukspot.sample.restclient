package com.rukspot.sample.restclient;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceStub;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

public class TenantMgt {
    TenantMgtAdminServiceStub stub;
    private static final Log log = LogFactory.getLog(TenantMgt.class);

    public TenantMgt(String user, String pass, String endpoint) {

        try {
            stub = new TenantMgtAdminServiceStub(endpoint + "/services/TenantMgtAdminService");
        } catch (AxisFault e) {
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

    public void addTenant(String domainName, String password, String firstName)
            throws RemoteException, TenantMgtAdminServiceExceptionException {
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        TenantInfoBean tenantInfoBean = new TenantInfoBean();
        tenantInfoBean.setActive(true);
        tenantInfoBean.setEmail(firstName + "@" + domainName);
        tenantInfoBean.setAdmin(firstName);
        tenantInfoBean.setAdminPassword(password);
        tenantInfoBean.setUsagePlan("");
        tenantInfoBean.setLastname(firstName + "wso2automation");
        tenantInfoBean.setSuccessKey("true");
        tenantInfoBean.setCreatedDate(calendar);
        tenantInfoBean.setTenantDomain(domainName);
        tenantInfoBean.setFirstname(firstName);

        try {
            TenantInfoBean tenantInfoBeanGet = this.stub.getTenant(domainName);
            if (!tenantInfoBeanGet.getActive() && tenantInfoBeanGet.getTenantId() != 0) {
                this.stub.activateTenant(domainName);
                log.info("Tenant domain " + domainName + " Activated successfully");
            } else if (!tenantInfoBeanGet.getActive() && tenantInfoBeanGet.getTenantId() == 0) {
                this.stub.addTenant(tenantInfoBean);
                this.stub.activateTenant(domainName);
                log.info("Tenant domain " + domainName + " created and activated successfully");
            } else {
                log.info("Tenant domain " + domainName + " already registered");
            }

        } catch (RemoteException var10) {
            System.out.println("RemoteException thrown while adding user/tenants : ");
            throw new RemoteException("RemoteException thrown while adding user/tenants : ", var10);
        }
    }

    public static void main(String[] args) throws Exception {
        TenantMgt tenantMgt = new TenantMgt("admin", "admin", Settings.BASE_URL);
    }

}
