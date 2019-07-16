package com.solace.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import org.json.*;

@ConditionalOnCloudPlatform(value = CloudPlatform.CLOUD_FOUNDRY)
public class CloudCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annoMetaData) {
        
        
        String VCAP_SERVICES = context.getEnvironment().getProperty("VCAP_SERVICES");
        if (VCAP_SERVICES != null) {
            try {
                JSONObject obj = new JSONObject(VCAP_SERVICES);
                JSONArray solaceServices = obj.getJSONArray("solace-pubsub");
                return (solaceServices.length() > 0);
            } catch (JSONException e) {
                // Found an issue
            }
        }
        return false;
    }
}
