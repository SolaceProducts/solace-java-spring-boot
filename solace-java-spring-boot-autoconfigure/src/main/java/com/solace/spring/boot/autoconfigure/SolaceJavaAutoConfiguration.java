/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.solace.spring.boot.autoconfigure;

import com.solace.services.loader.SolaceCredentialsLoader;
import com.solace.services.loader.model.SolaceServiceCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Configuration
@AutoConfigureBefore(JmsAutoConfiguration.class)
@AutoConfigureAfter(SolaceJavaAutoCloudConfiguration.class)
@ConditionalOnClass({JCSMPProperties.class})
@ConditionalOnMissingBean(SpringJCSMPFactory.class)
@EnableConfigurationProperties(SolaceJavaProperties.class)
public class SolaceJavaAutoConfiguration {

    @Autowired
	private SolaceJavaProperties properties;

    private SolaceCredentialsLoader solaceServicesInfoLoader = new SolaceCredentialsLoader();

	private SolaceServiceCredentials findFirstSolaceServiceCredentials() {
        SolaceServiceCredentials credentials = solaceServicesInfoLoader.getSolaceServiceInfo();
        return credentials != null ? credentials : new SolaceServiceCredentials();
    }

    @Bean
    public SpringJCSMPFactory connectionFactory() {
	    return connectionFactory(findFirstSolaceServiceCredentials());
    }

    public SpringJCSMPFactory connectionFactory(SolaceServiceCredentials solaceServiceCredentials) {
		Properties p = new Properties();
        Set<Map.Entry<String,String>> set = properties.getApiProperties().entrySet();
        for (Map.Entry<String,String> entry : set) {
            p.put("jcsmp." + entry.getKey(), entry.getValue());
        }
        JCSMPProperties jcsmpProps = createFromApiProperties(p);

        if (solaceServiceCredentials.getSmfHosts() != null && !solaceServiceCredentials.getSmfHosts().isEmpty())
            jcsmpProps.setProperty(JCSMPProperties.HOST, solaceServiceCredentials.getSmfHosts().get(0));
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