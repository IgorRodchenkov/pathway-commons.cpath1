package org.mskcc.pathdb.util;

import org.apache.xerces.impl.dv.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 Hash Utility Class.
 *
 * @author Ethan Cerami
 */
public class Md5Util {

    /**
     * Creates an MD5 Hash of the specified String, and returns
     * it as a Base64 Encoded String.
     *
     * @param data String data.
     * @return Base64 Encoded MD5 Hash.
     * @throws NoSuchAlgorithmException MD5 Hash Algorithm Not Found
     */
    public static String createMd5Hash(String data)
            throws NoSuchAlgorithmException {
        MessageDigest mac = MessageDigest.getInstance("MD5");
        byte bytes[] = data.getBytes();
        mac.reset();
        mac.update(bytes);
        byte hash[] = mac.digest();
        return Base64.encode(hash).trim();
    }

}
