package com.solace.spring.boot.autoconfigure;

import com.solace.services.loader.model.SolaceServiceCredentials;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

abstract class SolaceJavaAutoConfigurationBase {
    public SpringJCSMPFactory getSpringJCSMPFactory(SolaceServiceCredentials solaceServiceCredentials, SolaceJavaProperties properties) {
        Properties p = new Properties();
        Set<Map.Entry<String,String>> set = properties.getApiProperties().entrySet();
        for (Map.Entry<String,String> entry : set) {
            p.put("jcsmp." + entry.getKey(), entry.getValue());
        }
        JCSMPProperties jcsmpProps = createFromApiProperties(p);

        if (solaceServiceCredentials.getSmfHost() != null)
            jcsmpProps.setProperty(JCSMPProperties.HOST, solaceServiceCredentials.getSmfHost());
        else
            jcsmpProps.setProperty(JCSMPProperties.HOST, properties.getHost());

        if (solaceServiceCredentials.getMsgVpnName() != null)
            jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, solaceServiceCredentials.getMsgVpnName());
        else
            jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, properties.getMsgVpn());

        if (solaceServiceCredentials.getClientUsername() != null)
            jcsmpProps.setProperty(JCSMPProperties.USERNAME, solaceServiceCredentials.getClientUsername());
        else
            jcsmpProps.setProperty(JCSMPProperties.USERNAME, properties.getClientUsername());

        if (solaceServiceCredentials.getClientPassword() != null)
            jcsmpProps.setProperty(JCSMPProperties.PASSWORD, solaceServiceCredentials.getClientPassword());
        else
            jcsmpProps.setProperty(JCSMPProperties.PASSWORD, properties.getClientPassword());

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
