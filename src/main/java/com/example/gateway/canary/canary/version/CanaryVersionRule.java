package com.example.gateway.canary.canary.version;

import com.example.gateway.canary.canary.CanaryProperties;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.core.Ordered;

/**
 * @author hushengbin
 * @date 2022-04-25 11:07
 */
public interface CanaryVersionRule extends Ordered {

    String getVersion(
            RequestData requestData, CanaryProperties.CanaryService canaryService);

    @Override
    default int getOrder() {
        return 0;
    }
}
