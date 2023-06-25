package com.example.gateway.canary.canary;

import java.util.List;
import java.util.stream.Collectors;

import com.example.gateway.canary.canary.version.CanaryVersionRule;
import com.example.gateway.canary.canary.constant.CanaryMetadataConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

public class CanaryServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

  private static final Log log = LogFactory.getLog(CanaryServiceInstanceListSupplier.class);

  private final CanaryProperties canaryProperties;

  private final CanaryVersionRule canaryVersionRule;

  public CanaryServiceInstanceListSupplier(
      ServiceInstanceListSupplier delegate,
      CanaryProperties canaryProperties,
      CanaryVersionRule canaryVersionRule) {
    super(delegate);
    this.canaryProperties = canaryProperties;
    this.canaryVersionRule = canaryVersionRule;
  }

  @Override
  public Flux<List<ServiceInstance>> get() {
    return delegate.get();
  }

  @Override
  public Flux<List<ServiceInstance>> get(Request request) {
    if (!canaryProperties.isEnabled()) {
      return delegate.get(request);
    }

    return delegate
        .get(request)
        .mapNotNull(
            instances -> {
              try {
                return filteredByHintVersion(instances, getHintVersion(request));
              } catch (Exception e) {
                if (log.isWarnEnabled()) {
                  log.warn("filteredByHintVersion error", e);
                }
                return instances;
              }
            });
  }

  private String getHintVersion(Request request) {
    if (request == null || !(request.getContext() instanceof RequestDataContext)) {
      return CanaryMetadataConstant.VersionStatus.UN_KNOW.getName();
    }

    CanaryProperties.CanaryService canaryService =
        canaryProperties.getCanaryService(getServiceId());
    if (canaryService == null) {
      return CanaryMetadataConstant.VersionStatus.UN_KNOW.getName();
    }

    return canaryVersionRule.getVersion(
        ((RequestDataContext) request.getContext()).getClientRequest(), canaryService);
  }

  private List<ServiceInstance> filteredByHintVersion(
      List<ServiceInstance> instances, String hintVersion) {
    if (log.isTraceEnabled()) {
      log.trace(String.format("serviceId: %s, hintVersion: %s", getServiceId(), hintVersion));
    }

    if (CanaryMetadataConstant.VersionStatus.UN_KNOW.getName().equals(hintVersion)) {
      return instances;
    }

    List<ServiceInstance> hintInstances =
        instances.stream()
            .filter(
                i ->
                    i.getMetadata()
                        .getOrDefault(
                            CanaryMetadataConstant.VERSION,
                            CanaryMetadataConstant.VersionStatus.DEFAULT.getName())
                        .equals(hintVersion))
            .collect(Collectors.toList());
    if (hintInstances.isEmpty()) {
      if (log.isWarnEnabled()) {
        log.warn(String.format("hint instances is empty. hintVersion:%s", hintVersion));
      }
      return instances;
    } else {
      return hintInstances;
    }
  }
}
