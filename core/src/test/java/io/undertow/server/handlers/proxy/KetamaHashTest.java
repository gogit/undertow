package io.undertow.server.handlers.proxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class KetamaHashTest {

    @Test
    public void test()throws Exception{
        int routes = 3;
        for(int i=0; i < 10000; i++) {
            String consistentHashKey = UUID.randomUUID().toString();
            int index = KetamaHash.md5HashingAlg(consistentHashKey) % 3;
            Assert.assertTrue(index >=0 && index < 3);
        }
    }
}
