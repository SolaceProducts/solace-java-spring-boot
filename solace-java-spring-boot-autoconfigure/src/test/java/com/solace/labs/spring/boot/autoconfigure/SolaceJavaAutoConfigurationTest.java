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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPChannelProperties;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

public class SolaceJavaAutoConfigurationTest {

	private AnnotationConfigApplicationContext context;

	@After
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void defaultNativeConnectionFactory() throws InvalidPropertiesException {
		load(EmptyConfiguration.class, "");
		SpringJCSMPFactory jcsmpFactory = this.context
				.getBean(SpringJCSMPFactory.class);
		JCSMPSession session = jcsmpFactory.createSession();
		
		assertEquals("localhost", (String)session.getProperty(JCSMPProperties.HOST));
        assertEquals("default", (String)session.getProperty(JCSMPProperties.VPN_NAME));
        assertEquals("spring-default-client-username", (String)session.getProperty(JCSMPProperties.USERNAME) );
        assertEquals("", (String)session.getProperty(JCSMPProperties.PASSWORD));
        assertNotNull((String)session.getProperty(JCSMPProperties.CLIENT_NAME));
        // Channel properties
        JCSMPChannelProperties cp = (JCSMPChannelProperties) session
                .getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
        assertEquals(0, (int)cp.getConnectRetries());
        assertEquals(3, (int)cp.getReconnectRetries());
        assertEquals(0, (int)cp.getConnectRetriesPerHost());
        assertEquals(3000, (int)cp.getReconnectRetryWaitInMillis());
    }

	@Test
	public void customNativeConnectionFactory() throws InvalidPropertiesException {
		load(EmptyConfiguration.class, "solace.java.host=192.168.1.80:55500",
				"solace.java.clientUsername=bob", "solace.java.clientPassword=password",
				"solace.java.msgVpn=newVpn", "solace.java.clientName=client-name",
				"solace.java.connectRetries=1", "solace.java.reconnectRetries=5",
				"solace.java.connectRetriesPerHost=20", "solace.java.reconnectRetryWaitInMillis=1000");
		
		SpringJCSMPFactory jcsmpFactory = this.context
                .getBean(SpringJCSMPFactory.class);
        JCSMPSession session = jcsmpFactory.createSession();
        
        assertEquals("192.168.1.80:55500", (String)session.getProperty(JCSMPProperties.HOST));
        assertEquals("newVpn", (String)session.getProperty(JCSMPProperties.VPN_NAME));
        assertEquals("bob", (String)session.getProperty(JCSMPProperties.USERNAME) );
        assertEquals("password", (String)session.getProperty(JCSMPProperties.PASSWORD) );
        assertEquals("client-name", (String)session.getProperty(JCSMPProperties.CLIENT_NAME) );
        // Channel properties
        JCSMPChannelProperties cp = (JCSMPChannelProperties) session
                .getProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES);
        assertEquals(1, (int)cp.getConnectRetries());
        assertEquals(5, (int)cp.getReconnectRetries());
        assertEquals(20, (int)cp.getConnectRetriesPerHost());
        assertEquals(1000, (int)cp.getReconnectRetryWaitInMillis());
	}



	@Configuration
	static class EmptyConfiguration {}

	private void load(Class<?> config, String... environment) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(applicationContext, environment);
		applicationContext.register(config);
		applicationContext.register(SolaceJavaAutoConfiguration.class);
		applicationContext.refresh();
		this.context = applicationContext;
	}

}
