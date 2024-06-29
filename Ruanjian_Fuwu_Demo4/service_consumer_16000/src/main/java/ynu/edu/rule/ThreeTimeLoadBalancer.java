package ynu.edu.rule;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;

public class ThreeTimeLoadBalancer implements ReactorServiceInstanceLoadBalancer {


    private int instance_call_count = 0;  //已经被调用的次数
    private int instance_index = 0;  //当前提供服务的实例
    private final String serviceId;
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    public ThreeTimeLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = this.serviceInstanceListSupplierProvider.getIfAvailable();

        return supplier.get().next().map(this::getInstanceResponse);
    }

    //每个服务访问三次
    public Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instanceList){
        if(instanceList.isEmpty()){
            return new EmptyResponse();
        }
        int size = instanceList.size();

        ServiceInstance serviceInstance = null;


        while(serviceInstance == null){
            if(this.instance_call_count < 3){
                serviceInstance = instanceList.get(this.instance_index);
                this.instance_call_count++ ;
            }
            else{
                this.instance_index++ ;
                this.instance_call_count = 0;
                if (this.instance_index >= size){
                    this.instance_index = 0;
                }
            }
        }
        return new DefaultResponse(serviceInstance);
    }
}
