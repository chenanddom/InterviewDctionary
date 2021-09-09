package com.itdom.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 一句话的功能说明
 * <p>
 *
 * @author administer
 * @date 2021/9/9
 * @since 1.0.0
 */
@Service
public class IpInfoService {
    @SentinelResource(value = "ip_info",blockHandler = "exceptionHandler")
    public  Object getIpInfo(){
        String result="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            result = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }
    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public Object exceptionHandler(BlockException ex) {
        ex.printStackTrace();
        return new String("请求过于频繁");
    }
}
