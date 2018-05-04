package io.undertow.server.handlers.proxy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KetamaHash {

    /**
     * Calculates the ketama hash value for a string
     *
     * @param key
     * @return
     */
    public static int md5HashingAlg(String key) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(key.getBytes());
        byte[] bKey = md5.digest();
        int res = ((bKey[3] & 0xFF) << 24)
                | ((bKey[2] & 0xFF) << 16)
                | ((bKey[1] & 0xFF) << 8) | (bKey[0] & 0xFF);
        return res > 0 ? res : (-1 * res);
    }
}
