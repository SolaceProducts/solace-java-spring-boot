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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.solace.spring.cloud.core.SolaceMessagingInfo;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory;

public class SolaceJavaAutoCloudConfigurationTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

	private AnnotationConfigApplicationContext context;

	// Just enough to satisfy the Cloud Condition we need
	private static String CF_CLOUD_APP_ENV = "VCAP_APPLICATION={}";

	// Some other Service
	private static String CF_VCAP_SERVICES_OTHER = "VCAP_SERVICES={ otherService: [ { id: '1' } , { id: '2' } ]}";

	CloudCondition cloudCondition = new CloudCondition();

	@After
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void notCloudNoSpringJCSMPFactoryCloudFactory() throws NoSuchBeanDefinitionException {
		try {
			load(EmptyCloudConfiguration.class, "");

		this.context.getBean(SpringJCSMPFactoryCloudFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactoryCloudFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void notCloudNoSpringJCSMPFactory() throws NoSuchBeanDefinitionException {
		load(EmptyCloudConfiguration.class, "");

		try {
			this.context.getBean(SpringJCSMPFactory.class);
		} catch(NoSuchBeanDefinitionException e) {
			assertTrue(e.getBeanType().isAssignableFrom(com.solacesystems.jcsmp.SpringJCSMPFactory.class));
			throw e;
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void isCloudNoServiceNoSpringJCSMPFactory() throws NoSuchBeanDefinitionException {
		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV);

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
		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV);

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
		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES_OTHER);

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
		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES_OTHER);

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
	
	private String addOneSolaceService() {
		// Make a service visible to the Cloud Connector, will end up using the
		// SolaceMessagingInfoCreator

		Map<String, Object> services = createOneService();
		JSONObject jsonMapObject = new JSONObject(services);
		String JSONString = jsonMapObject.toString();
		environmentVariables.set("VCAP_SERVICES", "{ \"solace-messaging\": [" + JSONString + "] }");
		return JSONString;
	}

	@Test
	public void isCloudHasServiceSpringJCSMPFactory() throws NoSuchBeanDefinitionException {

		makeCloudEnv();

		String JSONString = addOneSolaceService();
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

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

		String JSONString = addOneSolaceService();
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

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

		String JSONString = addOneSolaceService();
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES);

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

		String JSONString = addOneSolaceService();
		String CF_VCAP_SERVICES = "VCAP_SERVICES={ \"solace-messaging\": [" + JSONString + "] }";

		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES, "solace.java.host=192.168.1.80:55500", "solace.java.clientUsername=bob",
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
		assertEquals(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT,
				(String) session.getProperty(JCSMPProperties.MESSAGE_ACK_MODE));
		assertEquals(Boolean.TRUE, (Boolean) session.getProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS));
		assertEquals(Boolean.TRUE, (Boolean) session.getProperty(JCSMPProperties.TOPIC_DISPATCH));
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

		load(EmptyCloudConfiguration.class, CF_CLOUD_APP_ENV, CF_VCAP_SERVICES, "solace.java.host=192.168.1.80:55500", "solace.java.clientUsername=bob",
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
		assertEquals(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT,
				(String) session.getProperty(JCSMPProperties.MESSAGE_ACK_MODE));
		assertEquals(Boolean.TRUE, (Boolean) session.getProperty(JCSMPProperties.REAPPLY_SUBSCRIPTIONS));
		assertEquals(Boolean.TRUE, (Boolean) session.getProperty(JCSMPProperties.TOPIC_DISPATCH));
		// Channel properties
		JCSMPChannelProperties cp = (JCSMPChannelProperties) session
				.getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
		assertEquals(5, (int) cp.getConnectRetries());
		assertEquals(10, (int) cp.getReconnectRetries());
		assertEquals(40, (int) cp.getConnectRetriesPerHost());
		assertEquals(1000, (int) cp.getReconnectRetryWaitInMillis());
	}

	
	@Configuration
	static class EmptyCloudConfiguration {
	}

	private void load(Class<?> config, String... environment) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(applicationContext, environment);
		applicationContext.register(config);
		applicationContext.register(SolaceJavaAutoCloudConfiguration.class);
		applicationContext.refresh();
		this.context = applicationContext;
	}

	private Map<String, Object> createOneService() {
		Map<String, Object> exVcapServices = new HashMap<String, Object>();

		Map<String, Object> exCred = new HashMap<String, Object>();

		exCred.put("clientUsername", "sample-client-username");
		exCred.put("clientPassword", "sample-client-password");
		exCred.put("msgVpnName", "sample-msg-vpn");
		exCred.put("smfHosts", Arrays.asList("tcp://192.168.1.50:7000"));
		exCred.put("smfTlsHosts", Arrays.asList("tcps://192.168.1.50:7003", "tcps://192.168.1.51:7003"));
		exCred.put("smfZipHosts", Arrays.asList("tcp://192.168.1.50:7001"));
		exCred.put("webMessagingUris", Arrays.asList("http://192.168.1.50:80"));
		exCred.put("webMessagingTlsUris", Arrays.asList("https://192.168.1.50:80"));
		exCred.put("jmsJndiUris", Arrays.asList("smf://192.168.1.50:7000"));
		exCred.put("jmsJndiTlsUris", Arrays.asList("smfs://192.168.1.50:7003", "smfs://192.168.1.51:7003"));
		exCred.put("mqttUris", Arrays.asList("tcp://192.168.1.50:7020"));
		exCred.put("mqttTlsUris", Arrays.asList("ssl://192.168.1.50:7021", "ssl://192.168.1.51:7021"));
		exCred.put("mqttWsUris", Arrays.asList("ws://192.168.1.50:7022"));
		exCred.put("mqttWssUris", Arrays.asList("wss://192.168.1.50:7023", "wss://192.168.1.51:7023"));
		exCred.put("restUris", Arrays.asList("http://192.168.1.50:7018"));
		exCred.put("restTlsUris", Arrays.asList("https://192.168.1.50:7019"));
		exCred.put("amqpUris", Arrays.asList("amqp://192.168.1.50:7016"));
		exCred.put("amqpTlsUris", Arrays.asList("amqps://192.168.1.50:7017"));
		exCred.put("managementHostnames", Arrays.asList("vmr-Medium-VMR-0"));
		exCred.put("managementUsername", "sample-mgmt-username");
		exCred.put("managementPassword", "sample-mgmt-password");
		exCred.put("activeManagementHostname", "vmr-medium-web");

		exVcapServices.put("credentials", exCred);
		exVcapServices.put("label", "solace-messaging");
		exVcapServices.put("name", "test-service-instance-name");
		exVcapServices.put("plan", "vmr-shared");
		exVcapServices.put("provider", "Solace Systems");
		// no need to check for tags in terms of validation. It's more for
		exVcapServices.put("tags",
				Arrays.asList("solace", "solace-messaging", "rest", "mqtt", "mq", "queue", "jms", "messaging", "amqp"));

		return exVcapServices;
	}

}