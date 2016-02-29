package com.fatshaw.experiment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import request.StubRequest;
import response.StubResponse;

import java.math.BigDecimal;

/**
 * Created by xiaochaojie on 16/2/29.
 */
@Controller()
@RequestMapping("/protostuff")
public class ProtostufTest {

    @ResponseBody
    @RequestMapping("/stub")
    public StubResponse testStub(@RequestBody StubRequest request) {
        StubResponse stubResponse = new StubResponse();
        stubResponse.setA(1);
        stubResponse.setB("asdf");
        stubResponse.setC(BigDecimal.ONE);
        return stubResponse;
    }

    @ResponseBody
    @RequestMapping("/check")
    public String check() {
        return "OK";
    }

}
