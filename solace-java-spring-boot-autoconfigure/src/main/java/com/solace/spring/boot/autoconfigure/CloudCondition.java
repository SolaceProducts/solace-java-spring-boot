package com.solace.spring.boot.autoconfigure;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CloudCondition implements Condition {

	private CloudFactory cloudFactory = new CloudFactory();

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata annoMetaData) {
		try {
			@SuppressWarnings("unused")
			Cloud cloud = cloudFactory.getCloud();
			return true;
		} catch (CloudException e) {
			// no suitable cloud found
		}
		return false;
	}
}
