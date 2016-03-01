package com.fatshaw.experiment.stub;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Stopwatch;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import request.StubRequest;
import response.StubResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by xiaochaojie on 16/2/29.
 */
public class TestStub {

    @Test
    @Ignore
    public void test() throws IOException {

        StubRequest stubRequest = new StubRequest();
        stubRequest.setA(1);
        stubRequest.setB(RandomStringUtils.randomAlphabetic(100000));
        stubRequest.setC(BigDecimal.ONE);

        // json test
        HttpRequest httpRequest2 = new HttpRequest("http://localhost:8198/protostuff/check", HttpRequest.METHOD_POST);
        String resultStr =
                httpRequest2.connectTimeout(100000).readTimeout(10000)
                        .contentType("application/json").send(JSON.toJSONString
                        (stubRequest)).body();
        assertEquals("OK", resultStr);

        // protostuff lz-4 test
        HttpRequest httpRequest = new HttpRequest("http://localhost:8198/protostuff/stub", HttpRequest.METHOD_POST);
        byte[] result = httpRequest.connectTimeout(100000)
                .readTimeout(10000)
                .accept("application/x-protobuf-lz4")
                .contentType("application/json")
                .send(JSON.toJSONString(stubRequest))
                .bytes();

        LZ4BlockInputStream inputStream = new LZ4BlockInputStream(new ByteArrayInputStream(result));
        byte[] buffer = IOUtils.toByteArray(inputStream);
        Schema<StubResponse> schema = RuntimeSchema.getSchema(StubResponse.class);
        StubResponse value = schema.newMessage();
        ProtobufIOUtil.mergeFrom(buffer, value, schema);

        assertEquals(stubRequest.getB(), value.getB());

        //protostuff
        HttpRequest httpRequest3 = new HttpRequest("http://localhost:8198/protostuff/stub", HttpRequest.METHOD_POST);
        byte[] result3 = httpRequest3.connectTimeout(100000)
                .readTimeout(10000)
                .accept("application/x-protobuf")
                .contentType("application/json")
                .send(JSON.toJSONString(stubRequest))
                .bytes();

        schema = RuntimeSchema.getSchema(StubResponse.class);
        value = schema.newMessage();
        ProtobufIOUtil.mergeFrom(result3, value, schema);
        assertEquals(stubRequest.getB(), value.getB());
    }

    @Test
    public void testJsonSerDeser() {
        StubRequest stubRequest = new StubRequest();
        stubRequest.setA(1);
        stubRequest.setB(RandomStringUtils.randomAlphabetic(100000));
        stubRequest.setC(BigDecimal.ONE);

        Stopwatch stopwatch = Stopwatch.createStarted();
        String json = JSON.toJSONString(stubRequest);
        long serTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        StubRequest rsr = JSON.parseObject(json, StubRequest.class);
        long deserTime = stopwatch.elapsed(TimeUnit.MILLISECONDS) - serTime;
        System.out.println("json size = " + json.length() + ",ser = " + serTime + ",deserTime = " + deserTime);

        assertEquals(stubRequest.getB(), rsr.getB());
    }

    @Test
    public void testJavaSerDeser() {
        StubRequest stubRequest = new StubRequest();
        stubRequest.setA(1);
        stubRequest.setB(RandomStringUtils.randomAlphabetic(100000));
        stubRequest.setC(BigDecimal.ONE);

        Stopwatch stopwatch = Stopwatch.createStarted();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(stubRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long serTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        byte[] userABytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(userABytes);
        StubRequest fsq = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            fsq = (StubRequest) ois.readObject();
            long deserTime = stopwatch.elapsed(TimeUnit.MILLISECONDS) - serTime;
            System.out.println("java ser size = " + userABytes.length + ",ser = " + serTime + ",deserTime = " + deserTime);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(stubRequest.getB(), fsq.getB());

    }

    @Test
    public void testPbSerDeser() {
        StubRequest stubRequest = new StubRequest();
        stubRequest.setA(1);
        stubRequest.setB(RandomStringUtils.randomAlphabetic(100000));
        stubRequest.setC(BigDecimal.ONE);

        Stopwatch stopwatch = Stopwatch.createStarted();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        StubRequest value = null;
        try {
            ProtobufIOUtil.writeTo(baos, stubRequest, RuntimeSchema.getSchema(StubRequest.class), LinkedBuffer
                    .allocate());
            long serTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            value = new StubRequest();
            ProtobufIOUtil.mergeFrom(baos.toByteArray(), value, RuntimeSchema.getSchema(StubRequest.class));
            long deserTime = stopwatch.elapsed(TimeUnit.MILLISECONDS) - serTime;
            System.out.println("pb ser size = " + baos.toByteArray().length + ",ser = " + serTime + ",deserTime = " +
                    deserTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(stubRequest.getB(), value.getB());
    }

    @Test
    public void setUp() {
        testJavaSerDeser();
        testJsonSerDeser();
        testPbSerDeser();
    }
}
