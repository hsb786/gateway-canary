package com.example.gateway.canary.canary.version;

import com.example.gateway.canary.canary.CanaryProperties;
import com.example.gateway.canary.canary.constant.CanaryMetadataConstant;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import com.example.gateway.canary.canary.constant.HeaderConstant;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * @author hushengbin
 * @date 2022-04-25 11:13
 */
public class WeightCanaryVersionRule implements CanaryVersionRule {

    @Override
    public String getVersion(
            RequestData requestData, CanaryProperties.CanaryService canaryService) {
        if (!canaryService.isWeightEnabled()) {
            return CanaryMetadataConstant.VersionStatus.DEFAULT.getName();
        }

        return weightRoute(requestData.getHeaders(), canaryService.getVersions());
    }

    private static String weightRoute(
            HttpHeaders httpHeaders, List<CanaryProperties.CanaryService.CanaryVersion> versions) {
        String userId = httpHeaders.getFirst(HeaderConstant.USER_ID);
        String deviceId = httpHeaders.getFirst(HeaderConstant.DEVICE_ID);

        Integer hash =
                Stream.of(userId, deviceId)
                        .filter(StringUtils::hasLength)
                        .findFirst()
                        .map(i -> Math.abs(i.hashCode() % 100))
                        .orElse(ThreadLocalRandom.current().nextInt(100));

        return hintVersion(versions, hash);
    }

    private static String hintVersion(List<CanaryProperties.CanaryService.CanaryVersion> versions, int weight) {
        int sum = 0;
        for (CanaryProperties.CanaryService.CanaryVersion version : versions) {
            if (weight < (sum += version.getWeight())) {
                return version.getVersion();
            }
        }
        return CanaryMetadataConstant.VersionStatus.DEFAULT.getName();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
