package com.example.gateway.canary.canary.version;

import com.example.gateway.canary.canary.CanaryProperties;
import com.example.gateway.canary.canary.constant.CanaryMetadataConstant;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

/**
 * @author hushengbin
 * @date 2022-04-25 11:10
 */
public class HeaderParamCanaryVersionRule implements CanaryVersionRule {

    @Override
    public String getVersion(
            RequestData requestData, CanaryProperties.CanaryService canaryService) {
        return canaryService.getVersions().stream()
                .filter(item -> match(requestData.getHeaders(), item.getHeaderParam()))
                .findFirst()
                .map(CanaryProperties.CanaryService.CanaryVersion::getVersion)
                .orElse(CanaryMetadataConstant.VersionStatus.UN_KNOW.getName());
    }

    public static boolean match(HttpHeaders headers, Map<String, List<String>> headerParam) {
        if (CollectionUtils.isEmpty(headerParam)) {
            return false;
        }

        return headerParam.entrySet().stream()
                .anyMatch(item -> item.getValue().contains(headers.getFirst(item.getKey())));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
