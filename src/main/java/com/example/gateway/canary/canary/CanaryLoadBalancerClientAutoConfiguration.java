package com.example.gateway.canary.canary;


import com.example.gateway.canary.canary.version.CanaryVersionRule;
import com.example.gateway.canary.canary.version.CompositeCanaryVersionRule;
import com.example.gateway.canary.canary.version.HeaderParamCanaryVersionRule;
import com.example.gateway.canary.canary.version.WeightCanaryVersionRule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @author hushengbin
 * @date 2022-04-24 10:29
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CanaryProperties.class)
@LoadBalancerClients(defaultConfiguration = CanaryLoadBalancerClientConfiguration.class)
public class CanaryLoadBalancerClientAutoConfiguration {

    @Bean
    @Primary
    public CanaryVersionRule canaryVersionRule(List<CanaryVersionRule> canaryVersionRules) {
        return new CompositeCanaryVersionRule(canaryVersionRules);
    }

    @Bean
    public HeaderParamCanaryVersionRule headerParamCanaryVersionRule() {
        return new HeaderParamCanaryVersionRule();
    }

    @Bean
    public WeightCanaryVersionRule weightCanaryVersionRule() {
        return new WeightCanaryVersionRule();
    }
}
