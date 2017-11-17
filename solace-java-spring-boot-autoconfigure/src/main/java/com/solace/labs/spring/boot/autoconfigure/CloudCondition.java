package com.solace.labs.spring.boot.autoconfigure;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CloudCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata annoMetaData) {
		Environment env =  context.getEnvironment();
		if (env.getProperty("VCAP_APPLICATION") != null)
		{
			return true;
		}
		return false;
	}

}
