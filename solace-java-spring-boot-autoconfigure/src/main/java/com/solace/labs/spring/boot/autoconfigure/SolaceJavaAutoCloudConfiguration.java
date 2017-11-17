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
package com.solace.labs.spring.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.solace.labs.spring.cloud.core.SolaceMessagingInfo;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

import java.util.List;
import java.util.Properties;

@Configuration
@AutoConfigureBefore(SolaceJavaAutoConfiguration.class)
@ConditionalOnClass({JCSMPProperties.class,  CloudFactory.class})
@ConditionalOnMissingBean(SpringJCSMPFactory.class)
@EnableConfigurationProperties(SolaceJavaProperties.class)
@Conditional(CloudCondition.class)
public class SolaceJavaAutoCloudConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(SolaceJavaAutoCloudConfiguration.class);

    @Autowired
	private SolaceJavaProperties properties;
    
	@Bean
	public SolaceMessagingInfo findSolaceMessagingInfo() {
        CloudFactory cloudFactory = new CloudFactory();
		Cloud cloud = cloudFactory.getCloud();
		SolaceMessagingInfo solacemessaging = null;
		List<ServiceInfo> serviceInfos = cloud.getServiceInfos();
		for (ServiceInfo serviceInfo : serviceInfos) {
		    if (serviceInfo instanceof SolaceMessagingInfo) {
		        solacemessaging = (SolaceMessagingInfo) serviceInfo;
		    	logger.info("Found Cloud Solace Messaging Info" + solacemessaging);
		    }
		    // Stop when we find the first one...
		    // TODO: Consider annotation driven selection
		    if( solacemessaging != null )
		    	break;
		}
		
		if( solacemessaging == null ) {
			logger.error("Cloud Solace Messaging Info was not found, cannot auto-configure");
            throw new IllegalStateException("Unable to create SpringJCSMPFactory did not find SolaceMessagingInfo in the current cloud environment");
	    }

		return solacemessaging;
	}

	@Bean
	public SpringJCSMPFactory connectionFactory(SolaceMessagingInfo solacemessaging) {
	    
        JCSMPProperties jcsmpProps = createFromAdvanced(properties.getAdvanced());
        
        jcsmpProps.setProperty(JCSMPProperties.HOST, solacemessaging.getSmfHost());
        jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, solacemessaging.getMsgVpnName());
        
        
        if( solacemessaging.getClientPassword() != null )
        	jcsmpProps.setProperty(JCSMPProperties.USERNAME, solacemessaging.getClientUsername());
        else
        	jcsmpProps.setProperty(JCSMPProperties.USERNAME, properties.getClientUsername());
        
        if( solacemessaging.getClientPassword() != null )
        	jcsmpProps.setProperty(JCSMPProperties.PASSWORD, solacemessaging.getClientPassword());
        else
        	jcsmpProps.setProperty(JCSMPProperties.PASSWORD, properties.getClientPassword());
        
        jcsmpProps.setProperty(JCSMPProperties.MESSAGE_ACK_MODE,properties.getMessageAckMode());
        jcsmpProps.setProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS,properties.getReapplySubscriptions());
        
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
        SpringJCSMPFactory cf = new SpringJCSMPFactory(jcsmpProps);
        
	    return cf;
	}

	private JCSMPProperties createFromAdvanced(Properties advanced) {
        if (advanced != null) {
            return JCSMPProperties.fromProperties(advanced);
        } else {
            return new JCSMPProperties();
        }
    }

}