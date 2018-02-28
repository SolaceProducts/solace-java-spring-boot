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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.env.Environment;

import com.solace.spring.cloud.core.SolaceMessagingInfo;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory;

public class SolaceJavaAutoCloudConfigurationTest extends SolaceJavaAutoConfigurationTestBase {
	// Just enough to satisfy the Cloud Condition we need
	private static String CF_CLOUD_APP_ENV = "VCAP_APPLICATION={}";

	// Some other Service
	private static String CF_VCAP_SERVICES_OTHER = "VCAP_SERVICES={ otherService: [ { id: '1' } , { id: '2' } ]}";

	CloudCondition cloudCondition = new CloudCondition();

	public SolaceJavaAutoCloudConfigurationTest() {
		super(SolaceJavaAutoCloudConfiguration.class);
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void notCloudNoSpringJCSMPFactoryCloudFactory() throws NoSuchBeanDefinitionException {
		try {
			load("");

		this.context.getBean(SpringJCSMPFactoryCloudFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void notCloudNoSpringJCSMPFactory() throws NoSuchBeanDefinitionException {
		load("");

		try {
			this.context.getBean(SpringJCSMPFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void isCloudNoServiceNoSpringJCSMPFactory() throws NoSuchBeanDefinitionException {
		load(CF_CLOUD_APP_ENV);

		Environment env = context.getEnvironment();
		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNull(VCAP_SERVICES);

		try {
		this.context.getBean(SpringJCSMPFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void isCloudNoServiceNoSpringJCSMPFactoryCloudFactory() throws NoSuchBeanDefinitionException {
		load(CF_CLOUD_APP_ENV);

		Environment env = context.getEnvironment();
		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNull(VCAP_SERVICES);

		try {
			this.context.getBean(SpringJCSMPFactoryCloudFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void isCloudWrongServiceNoSpringJCSMPFactory() throws NoSuchBeanDefinitionException {
		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES_OTHER);

		Environment env = context.getEnvironment();
		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNotNull(VCAP_SERVICES);
		assertFalse(VCAP_SERVICES.contains("solace-messaging"));

		try {
			this.context.getBean(SpringJCSMPFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void isCloudWrongServiceNoSpringJCSMPFactoryCloudFactory() throws NoSuchBeanDefinitionException {
		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES_OTHER);

		Environment env = context.getEnvironment();
		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNotNull(VCAP_SERVICES);
		assertFalse(VCAP_SERVICES.contains("solace-messaging"));

		try {
			this.context.getBean(SpringJCSMPFactoryCloudFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory.class));
			throw e;
		}

	}

	private void makeCloudEnv() {
		// To force the detection of spring cloud connector which uses
		// EnvironmentAccessor
		environmentVariables.set("VCAP_APPLICATION", "{}");
		assertEquals("{}", System.getenv("VCAP_APPLICATION"));
	}

	@Test
	public void isCloudHasServiceSpringJCSMPFactory() throws NoSuchBeanDefinitionException {

		makeCloudEnv();

		String JSONString = addOneSolaceService("VCAP_SERVICES");
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

		Environment env = context.getEnvironment();

		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNotNull(VCAP_SERVICES);
		assertTrue(VCAP_SERVICES.contains("solace-messaging"));

		assertNotNull(this.context.getBean(SpringJCSMPFactory.class));
	}

	@Test
	public void isCloudHasServiceSpringJCSMPFactoryCloudFactory() throws NoSuchBeanDefinitionException {

		makeCloudEnv();

		String JSONString = addOneSolaceService("VCAP_SERVICES");
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

		Environment env = context.getEnvironment();

		String VCAP_APPLICATION = env.getProperty("VCAP_APPLICATION");
		assertNotNull(VCAP_APPLICATION);
		assertEquals("{}", VCAP_APPLICATION);

		String VCAP_SERVICES = env.getProperty("VCAP_SERVICES");
		assertNotNull(VCAP_SERVICES);
		assertTrue(VCAP_SERVICES.contains("solace-messaging"));

		SpringJCSMPFactoryCloudFactory springJCSMPFactoryCloudFactory = this.context.getBean(SpringJCSMPFactoryCloudFactory.class);
		assertNotNull(springJCSMPFactoryCloudFactory);

		//
		assertNotNull(springJCSMPFactoryCloudFactory.getSpringJCSMPFactory());

		List<SolaceMessagingInfo> availableServices = springJCSMPFactoryCloudFactory.getSolaceMessagingInfos();

		assertNotNull(availableServices);

		assertEquals(1,availableServices.size());

	}

	@Test
	public void isCloudConfiguredBySolaceMessagingInfoAndDefaultsForOtherProperties() throws InvalidPropertiesException {

		makeCloudEnv();

		String JSONString = addOneSolaceService("VCAP_SERVICES");
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

		SpringJCSMPFactoryCloudFactory springJCSMPFactoryCloudFactory = this.context
				.getBean(SpringJCSMPFactoryCloudFactory.class);
		assertNotNull(springJCSMPFactoryCloudFactory);

		SpringJCSMPFactory jcsmpFactory = this.context.getBean(SpringJCSMPFactory.class);
		assertNotNull(jcsmpFactory);

		JCSMPSession session = jcsmpFactory.createSession();
		assertNotNull(session);

		// The are cloud provided (SolaceMessagingInfo) properties
		assertEquals("tcp://192.168.1.50:7000", (String) session.getProperty(JCSMPProperties.HOST));
		assertEquals("sample-msg-vpn", (String) session.getProperty(JCSMPProperties.VPN_NAME));
		assertEquals("sample-client-username", (String) session.getProperty(JCSMPProperties.USERNAME));
		assertEquals("sample-client-password", (String) session.getProperty(JCSMPProperties.PASSWORD));

		// Other non cloud (SolaceMessagingInfo) provided properties
		assertEquals(JCSMPProperties.SUPPORTED_MESSAGE_ACK_AUTO,
				(String) session.getProperty(JCSMPProperties.MESSAGE_ACK_MODE));
		assertEquals(Boolean.FALSE, (Boolean) session.getProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS));
		assertNotNull((String) session.getProperty(JCSMPProperties.CLIENT_NAME));
		// Channel properties
		JCSMPChannelProperties cp = (JCSMPChannelProperties) session
				.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
		assertEquals(1, (int) cp.getConnectRetries());
		assertEquals(5, (int) cp.getReconnectRetries());
		assertEquals(20, (int) cp.getConnectRetriesPerHost());
		assertEquals(3000, (int) cp.getReconnectRetryWaitInMillis());
	}

	@Test
	public void isCloudConfiguredBySolaceMessagingInfoAndOtherProperties() throws InvalidPropertiesException {
		makeCloudEnv();

		String JSONString = addOneSolaceService("VCAP_SERVICES");
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES, "solace.java.host=192.168.1.80:55500", "solace.java.clientUsername=bob",
				"solace.java.clientPassword=password", "solace.java.msgVpn=newVpn",
				"solace.java.clientName=client-name", "solace.java.connectRetries=5", "solace.java.reconnectRetries=10",
				"solace.java.connectRetriesPerHost=40", "solace.java.reconnectRetryWaitInMillis=1000",
				"solace.java.messageAckMode=client_ack", "solace.java.reapplySubscriptions=true",
				"solace.java.advanced.jcsmp.TOPIC_DISPATCH=true");

		SpringJCSMPFactory jcsmpFactory = this.context.getBean(SpringJCSMPFactory.class);
		JCSMPSession session = jcsmpFactory.createSession();

		// The are cloud provided (SolaceMessagingInfo) properties
		assertEquals("tcp://192.168.1.50:7000", (String) session.getProperty(JCSMPProperties.HOST));
		assertEquals("sample-msg-vpn", (String) session.getProperty(JCSMPProperties.VPN_NAME));
		assertEquals("sample-client-username", (String) session.getProperty(JCSMPProperties.USERNAME));
		assertEquals("sample-client-password", (String) session.getProperty(JCSMPProperties.PASSWORD));

		// Other non cloud provided properties..
		assertEquals("client-name", (String) session.getProperty(JCSMPProperties.CLIENT_NAME));
		// Channel properties
		JCSMPChannelProperties cp = (JCSMPChannelProperties) session
				.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
		assertEquals(5, (int) cp.getConnectRetries());
		assertEquals(10, (int) cp.getReconnectRetries());
		assertEquals(40, (int) cp.getConnectRetriesPerHost());
		assertEquals(1000, (int) cp.getReconnectRetryWaitInMillis());
	}

	@Test
	public void isCloudConfiguredBySolaceMessagingInfoAndOtherPropertiesWhenMissingCredentials() throws InvalidPropertiesException {
		makeCloudEnv();

		Map<String, Object> services = createOneService();
		@SuppressWarnings("unchecked")
		Map<String, Object> credentials = (Map<String, Object>)services.get("credentials");
		credentials.remove("clientUsername");
		credentials.remove("clientPassword");


		JSONObject jsonMapObject = new JSONObject(services);
		String JSONString = jsonMapObject.toString();
		environmentVariables.set("VCAP_SERVICES", "{ \"solace-messaging\": [" + JSONString + "] }");


		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(CF_CLOUD_APP_ENV, CF_VCAP_SERVICES, "solace.java.host=192.168.1.80:55500", "solace.java.clientUsername=bob",
				"solace.java.clientPassword=password", "solace.java.msgVpn=newVpn",
				"solace.java.clientName=client-name", "solace.java.connectRetries=5", "solace.java.reconnectRetries=10",
				"solace.java.connectRetriesPerHost=40", "solace.java.reconnectRetryWaitInMillis=1000",
				"solace.java.messageAckMode=client_ack", "solace.java.reapplySubscriptions=true",
				"solace.java.advanced.jcsmp.TOPIC_DISPATCH=true");

		SpringJCSMPFactory jcsmpFactory = this.context.getBean(SpringJCSMPFactory.class);
		JCSMPSession session = jcsmpFactory.createSession();

		// The are cloud provided (SolaceMessagingInfo) properties
		assertEquals("tcp://192.168.1.50:7000", (String) session.getProperty(JCSMPProperties.HOST));
		assertEquals("sample-msg-vpn", (String) session.getProperty(JCSMPProperties.VPN_NAME));
		assertEquals("bob", (String) session.getProperty(JCSMPProperties.USERNAME));
		assertEquals("password", (String) session.getProperty(JCSMPProperties.PASSWORD));

		// Other non cloud provided properties..
		assertEquals("client-name", (String) session.getProperty(JCSMPProperties.CLIENT_NAME));
		// Channel properties
		JCSMPChannelProperties cp = (JCSMPChannelProperties) session
				.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
		assertEquals(5, (int) cp.getConnectRetries());
		assertEquals(10, (int) cp.getReconnectRetries());
		assertEquals(40, (int) cp.getConnectRetriesPerHost());
		assertEquals(1000, (int) cp.getReconnectRetryWaitInMillis());
	}

}