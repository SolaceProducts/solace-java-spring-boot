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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.solace.spring.cloud.core.SolaceMessagingInfo;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory;

@Configuration
@AutoConfigureBefore(SolaceJavaAutoConfiguration.class)
@ConditionalOnClass({ JCSMPProperties.class, CloudFactory.class })
@ConditionalOnMissingBean(SpringJCSMPFactory.class)
@EnableConfigurationProperties(SolaceJavaProperties.class)
@Conditional(CloudCondition.class)
public class SolaceJavaAutoCloudConfiguration implements SpringJCSMPFactoryCloudFactory {

	private static final Logger logger = LoggerFactory.getLogger(SolaceJavaAutoCloudConfiguration.class);

	private CloudFactory cloudFactory = new CloudFactory();

	@Autowired
	private SolaceJavaProperties properties;

	@Bean
	public List<SolaceMessagingInfo> getSolaceMessagingInfos() {
		List<SolaceMessagingInfo> solaceMessagingInfoList = new ArrayList<>();
		
		Cloud cloud = cloudFactory.getCloud();
		
		List<ServiceInfo> serviceInfos = cloud.getServiceInfos();
		for (ServiceInfo serviceInfo : serviceInfos) {
			if (serviceInfo instanceof SolaceMessagingInfo) {
				solaceMessagingInfoList.add((SolaceMessagingInfo) serviceInfo);
			}
		}
		return solaceMessagingInfoList;
	}

	@Bean
	public SolaceMessagingInfo findFirstSolaceMessagingInfo() {
		SolaceMessagingInfo solacemessaging = null;
		Cloud cloud = cloudFactory.getCloud();
		List<ServiceInfo> serviceInfos = cloud.getServiceInfos();
		for (ServiceInfo serviceInfo : serviceInfos) {
			// Stop when we find the first one...
			// TODO: Consider annotation driven selection, or sorted plan based
			// selection
			if (serviceInfo instanceof SolaceMessagingInfo) {
				solacemessaging = (SolaceMessagingInfo) serviceInfo;
				logger.info("Found Cloud Solace Messaging Service Instance Id: " + solacemessaging.getId());
				break;
			}
		}

		if (solacemessaging == null) {
			// The CloudCondition should shield from this happening, should not
			// arrive to this state.
			logger.error("Cloud Solace Messaging Info was not found, cannot auto-configure");
			throw new IllegalStateException(
					"Unable to create SpringJCSMPFactory did not find SolaceMessagingInfo in the current cloud environment");
		}

		return solacemessaging;
	}

	@Bean
	public SpringJCSMPFactory getSpringJCSMPFactory() {
		return getSpringJCSMPFactory(findFirstSolaceMessagingInfo());
	}

	@Override
	public SpringJCSMPFactory getSpringJCSMPFactory(SolaceMessagingInfo solacemessaging) {

		Properties p = new Properties();
        Set<Map.Entry<String,String>> set = properties.getApiProperties().entrySet();
        for (Map.Entry<String,String> entry : set) {
            p.put("jcsmp." + entry.getKey(), entry.getValue());
        }
        JCSMPProperties jcsmpProps = createFromApiProperties(p);

		if (solacemessaging.getSmfHost() != null)
			jcsmpProps.setProperty(JCSMPProperties.HOST, solacemessaging.getSmfHost());
		else
			jcsmpProps.setProperty(JCSMPProperties.HOST, properties.getHost());

		if (solacemessaging.getMsgVpnName() != null)
			jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, solacemessaging.getMsgVpnName());
		else
			jcsmpProps.setProperty(JCSMPProperties.VPN_NAME, properties.getMsgVpn());

		if (solacemessaging.getClientPassword() != null)
			jcsmpProps.setProperty(JCSMPProperties.USERNAME, solacemessaging.getClientUsername());
		else
			jcsmpProps.setProperty(JCSMPProperties.USERNAME, properties.getClientUsername());

		if (solacemessaging.getClientPassword() != null)
			jcsmpProps.setProperty(JCSMPProperties.PASSWORD, solacemessaging.getClientPassword());
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