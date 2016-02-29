package com.fatshaw.experiment.stub;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.Ignore;
import org.junit.Test;
import request.StubRequest;
import response.StubResponse;

import java.math.BigDecimal;

/**
 * Created by xiaochaojie on 16/2/29.
 */
public class TestStub {

    @Test
    @Ignore
    public void test() {

        StubRequest stubRequest = new StubRequest();
        stubRequest.setA(1);
        stubRequest.setB("abc");
        stubRequest.setC(BigDecimal.ONE);
        System.out.println(JSON.toJSON(stubRequest));

        // json test
        HttpRequest httpRequest2 = new HttpRequest("http://localhost:8198/protostuff/check", HttpRequest.METHOD_POST);
        String resultStr =
                httpRequest2.connectTimeout(100000).readTimeout(10000)
                        .contentType("application/json").send(JSON.toJSONString
                        (stubRequest)).body();
        System.out.println(JSON.toJSON(resultStr));

        // protostuff test
        HttpRequest httpRequest = new HttpRequest("http://localhost:8198/protostuff/stub", HttpRequest.METHOD_POST);
        byte[] result =
                httpRequest.connectTimeout(100000).readTimeout(10000).accept("application/x-protobuf")
                        .contentType("application/json").send(JSON.toJSONString
                        (stubRequest)).bytes();

        Schema<StubResponse> schema = RuntimeSchema.getSchema(StubResponse.class);
        StubResponse value = schema.newMessage();
        ProtobufIOUtil.mergeFrom(result, value, schema);
        System.out.println(JSON.toJSON(value));
    }

}
