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

import com.solace.labs.spring.cloud.core.SolaceMessagingInfo;

/**
 * A Factory for SpringJCSMPFactory to Support Cloud Environments having
 * multiple solace-messaging services.
 */
public interface SpringJCSMPFactoryCloudFactory {

	/**
	 * Lists All Cloud Environment detected Solace Messaging services
	 * 
	 * @return
	 */
	public List<SolaceMessagingInfo> getSolaceMessagingInfos();

	/**
	 * Returns a SpringJCSMPFactory based on the first detected
	 * SolaceMessagingInfo
	 * 
	 * @return
	 */
	public SpringJCSMPFactory getSpringJCSMPFactory();

	/**
	 * Returns a SpringJCSMPFactory based on the given SolaceMessagingInfo
	 * 
	 * @param solaceMessagingInfo
	 * @return
	 */
	public SpringJCSMPFactory getSpringJCSMPFactory(SolaceMessagingInfo solaceMessagingInfo);

}
