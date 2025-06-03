package c.plus.plan.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fanwei on 1/23/22
 */
public class AesUtils {
    public static String encrypt(String data, String aesKey, String aesIv) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String baseData = encoder.encodeToString(data.getBytes());
        String baseSessionKey = encoder.encodeToString(aesKey.getBytes());
        String baseIv = encoder.encodeToString(aesIv.getBytes());
        Security.addProvider(new BouncyCastleProvider());
        return doEncrypt(baseData, baseSessionKey, baseIv);
    }

    public static String doEncrypt(String data, String sessionKey, String iv) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(data);
        byte[] keyByte = decoder.decode(sessionKey);
        byte[] ivByte = decoder.decode(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, params);
        byte[] result = cipher.doFinal(dataByte);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(result);
    }


    public static String decrypt(String encryptedData, String aesKey, String aesIV) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String baseSessionKey = encoder.encodeToString(aesKey.getBytes());
        String baseIv = encoder.encodeToString(aesIV.getBytes());
        Security.addProvider(new BouncyCastleProvider());
        return doDecrypt(encryptedData, baseSessionKey, baseIv);
    }


    public static String doDecrypt(String encryptedData, String sessionKey, String iv) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(encryptedData);
        byte[] keyByte = decoder.decode(sessionKey);
        byte[] ivByte = decoder.decode(iv);

        String data = null;
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);
        byte[] result = cipher.doFinal(dataByte);
        data = new String(result);

        return data;
    }
}
