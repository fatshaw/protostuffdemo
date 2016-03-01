package com.fatshaw.experiment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import request.StubRequest;
import response.StubResponse;

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
        stubResponse.setA(request.getA());
        stubResponse.setB(request.getB());
        stubResponse.setC(request.getC());
        return stubResponse;
    }

    @ResponseBody
    @RequestMapping("/check")
    public String check() {
        return "OK";
    }

}
