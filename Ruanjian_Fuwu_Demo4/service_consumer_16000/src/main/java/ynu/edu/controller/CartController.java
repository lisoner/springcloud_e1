package ynu.edu.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ynu.edu.entity.CommonResult;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

import java.sql.SQLDataException;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private ServiceProviderService serviceProviderService;

    @GetMapping("/getCartById/{userId}")
    @LoadBalanced
    @CircuitBreaker(name = "bakendA",fallbackMethod = "getCartByIdDown")
    public CommonResult<User> getCartById(@PathVariable("userId") Integer userId) {

        CommonResult<User> result = serviceProviderService.getUserById(userId);
        return result;
    }

    public CommonResult<User> getCartByIdDown(Integer userId , Throwable e){
        e.printStackTrace();
        String message = "获取用户"+userId+"信息的服务当前被熔断，因此方法降级";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<>(400,message,new User());
        return result;
    }


    public CommonResult<User> getCartByIdDown(Integer userId , SQLDataException e){
        e.printStackTrace();
        String message = "请联系管理员，当前数据库异常，因此方法降价";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<>(400,message,new User());
        return result;
    }



}

//服务访问url  http://10.244.97.127:16000/cart/getCartById/1
