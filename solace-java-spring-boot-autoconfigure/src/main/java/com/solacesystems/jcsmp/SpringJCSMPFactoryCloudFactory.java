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

package com.solacesystems.jcsmp;

import java.util.List;

import com.solace.services.loader.model.SolaceServiceCredentials;

/**
 * A Factory for SpringJCSMPFactory to Support Cloud Environments having
 * multiple solace-messaging services.
 */
public interface SpringJCSMPFactoryCloudFactory<T extends SolaceServiceCredentials> {
	/**
	 * Gets the first detected SolaceServiceCredentials
	 *
	 * @return A Solace Messaging service
	 */
	T findFirstSolaceServiceCredentials();

	/**
	 * Lists All Cloud Environment detected Solace Messaging services
	 *
	 * @return List of all Cloud Environment detected Solace Messaging services
	 */
	List<T> getSolaceServiceCredentials();

	/**
	 * Returns a SpringJCSMPFactory based on the first detected
	 * SolaceServiceCredentials
	 *
	 * @return SpringJCSMPFactory based on the first detected
	 *         SolaceServiceCredentials
	 */
	SpringJCSMPFactory getSpringJCSMPFactory();

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	SpringJCSMPFactory getSpringJCSMPFactory(String id);

	/**
	 * Returns a SpringJCSMPFactory based on the given SolaceServiceCredentials
	 *
	 * @param solaceServiceCredentials
	 * @return SpringJCSMPFactory based on the given SolaceServiceCredentials
	 */
	SpringJCSMPFactory getSpringJCSMPFactory(T solaceServiceCredentials);

	/**
	 *
	 * @return
	 */
	JCSMPProperties getJCSMPProperties();

	/**
	 *
	 * @param id
	 * @return
	 */
	JCSMPProperties getJCSMPProperties(String id);

	/**
	 *
	 * @param solaceServiceCredentials
	 * @return
	 */
	JCSMPProperties getJCSMPProperties(T solaceServiceCredentials);
}
