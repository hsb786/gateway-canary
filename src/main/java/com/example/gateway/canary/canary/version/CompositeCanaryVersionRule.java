package com.example.gateway.canary.canary.version;

import com.example.gateway.canary.canary.CanaryProperties;
import com.example.gateway.canary.canary.constant.CanaryMetadataConstant;

import java.util.List;

import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author hushengbin
 * @date 2022-04-25 11:48
 */
public class CompositeCanaryVersionRule implements CanaryVersionRule {

    private final List<CanaryVersionRule> canaryVersionRules;

    public CompositeCanaryVersionRule(List<CanaryVersionRule> canaryVersionRules) {
        AnnotationAwareOrderComparator.sort(canaryVersionRules);
        this.canaryVersionRules = canaryVersionRules;
    }

    @Override
    public String getVersion(
            RequestData requestData, CanaryProperties.CanaryService canaryService) {
        for (CanaryVersionRule rule : canaryVersionRules) {
            String version = rule.getVersion(requestData, canaryService);
            if (CanaryMetadataConstant.VersionStatus.UN_KNOW.getName().equals(version)) {
                continue;
            }
            return version;
        }

        return CanaryMetadataConstant.VersionStatus.UN_KNOW.getName();
    }
}
