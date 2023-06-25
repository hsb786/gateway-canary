package com.example.gateway.canary.canary;


import com.example.gateway.canary.canary.version.CanaryVersionRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author hushengbin
 * @date 2022-04-24 10:29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
public class CanaryLoadBalancerClientConfiguration {

    private static final int REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER = 173827465;

    private static final Log LOG = LogFactory.getLog(CanaryLoadBalancerClientConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnReactiveDiscoveryEnabled
    @Order(REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER)
    public static class ReactiveSupportConfiguration {

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        @ConditionalOnProperty(
                value = "spring.cloud.loadbalancer.configurations",
                havingValue = "default",
                matchIfMissing = true)
        public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            ServiceInstanceListSupplier serviceInstanceListSupplier =
                    ServiceInstanceListSupplier.builder().withDiscoveryClient().build(context);
            CanaryProperties canaryProperties = context.getBean(CanaryProperties.class);
            CanaryVersionRule canaryVersionRule = context.getBean(CanaryVersionRule.class);
            return new CanaryServiceInstanceListSupplier(serviceInstanceListSupplier, canaryProperties, canaryVersionRule);
        }
    }
}
