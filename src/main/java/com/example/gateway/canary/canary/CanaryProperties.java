package com.example.gateway.canary.canary;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author hushengbin
 * @date 2021-06-17 17:59
 */
@ConfigurationProperties(prefix = "canary")
public class CanaryProperties {

    private boolean enabled = false;

    private List<CanaryService> services = Collections.emptyList();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<CanaryService> getServices() {
        return services;
    }

    public void setServices(List<CanaryService> services) {
        this.services = services;
    }

    public static class CanaryService {

        private String serviceId;

        private boolean weightEnabled = false;

        private List<CanaryVersion> versions = Collections.emptyList();

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public boolean isWeightEnabled() {
            return weightEnabled;
        }

        public void setWeightEnabled(boolean weightEnabled) {
            this.weightEnabled = weightEnabled;
        }

        public List<CanaryVersion> getVersions() {
            return versions;
        }

        public void setVersions(List<CanaryVersion> versions) {
            this.versions = versions;
        }

        public static class CanaryVersion {

            private String version;

            private Map<String, List<String>> headerParam = Collections.emptyMap();

            private double weight;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public Map<String, List<String>> getHeaderParam() {
                return headerParam;
            }

            public void setHeaderParam(Map<String, List<String>> headerParam) {
                this.headerParam = headerParam;
            }

            public double getWeight() {
                return weight;
            }

            public void setWeight(double weight) {
                this.weight = weight;
            }
        }
    }

    public CanaryService getCanaryService(String serviceId) {
        return services == null
                ? null
                : services.stream()
                .filter(i -> i.getServiceId().equals(serviceId))
                .findFirst()
                .orElse(null);
    }
}
