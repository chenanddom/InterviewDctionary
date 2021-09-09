package com.itdom.controller;

import com.itdom.service.IpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一句话的功能说明
 * <p>
 *
 * @author administer
 * @date 2021/9/9
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sentinel")
public class SentinelController {

    @Autowired
    private IpInfoService ipInfoService;

    @GetMapping(value = "/ip")
    public Object welcome(){
        return ipInfoService.getIpInfo();
    }

}
