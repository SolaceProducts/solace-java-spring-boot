package com.solace.spring.boot.autoconfigure;

import com.solace.services.loader.model.SolaceServiceCredentials;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

abstract class SolaceJavaAutoConfigurationBase {
    private SolaceJavaProperties properties;

    SolaceJavaAutoConfigurationBase(SolaceJavaProperties properties) {
        this.properties = properties;
    }

    public SpringJCSMPFactory getSpringJCSMPFactory(SolaceServiceCredentials solaceServiceCredentials) {
        Properties p = new Properties();
        Set<Map.Entry<String,String>> set = properties.getApiProperties().entrySet();
        for (Map.Entry<String,String> entry : set) {
            p.put("jcsmp." + entry.getKey(), entry.getValue());
        }

        JCSMPProperties jcsmpProps = createFromApiProperties(p);

        jcsmpProps.setProperty(JCSMPProperties.HOST, solaceServiceCredentials.getSmfHost() != null ?
                    solaceServiceCredentials.getSmfHost() : properties.getHost());

        jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, solaceServiceCredentials.getMsgVpnName() != null ?
                solaceServiceCredentials.getMsgVpnName() : properties.getMsgVpn());

        jcsmpProps.setProperty(JCSMPProperties.USERNAME, solaceServiceCredentials.getClientUsername() != null ?
                solaceServiceCredentials.getClientUsername() : properties.getClientUsername());

        jcsmpProps.setProperty(JCSMPProperties.PASSWORD, solaceServiceCredentials.getClientPassword() != null ?
                solaceServiceCredentials.getClientPassword() : properties.getClientPassword());

        if ((properties.getClientName() != null) && (!properties.getClientName().isEmpty())) {
            jcsmpProps.setProperty(JCSMPProperties.CLIENT_NAME, properties.getClientName());
        }

        // Channel Properties
        JCSMPChannelProperties cp = (JCSMPChannelProperties) jcsmpProps
                .getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
        cp.setConnectRetries(properties.getConnectRetries());
        cp.setReconnectRetries(properties.getReconnectRetries());
        cp.setConnectRetriesPerHost(properties.getConnectRetriesPerHost());
        cp.setReconnectRetryWaitInMillis(properties.getReconnectRetryWaitInMillis());

        // Create the SpringJCSMPFactory
        return new SpringJCSMPFactory(jcsmpProps);
    }

    private JCSMPProperties createFromApiProperties(Properties apiProps) {
        if (apiProps != null) {
            return JCSMPProperties.fromProperties(apiProps);
        } else {
            return new JCSMPProperties();
        }
    }
}
