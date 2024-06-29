package ynu.edu.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class CustomLoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment envir, LoadBalancerClientFactory lbcf){
        String name = envir.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);


        return new RandomLoadBalancer(lbcf.getLazyProvider(name, ServiceInstanceListSupplier.class),name);
    }
}
