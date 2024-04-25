package cn.com.finance.ema.utils;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * <p>
 * AES加密
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
public class AESUtil {
    private static final byte[] name = {-16, -72, -103, -33, -114, 49, -65, -80, 14, 35, 68, -4, -50, 30, 93, -83};

    private static Key toKey(byte[] key)
            throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

    private static byte[] decrypt(byte[] data, byte[] key)
            throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(2, k);
        return cipher.doFinal(data);
    }

    private static byte[] encrypt(byte[] data, byte[] key)
            throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(1, k);
        return cipher.doFinal(data);
    }

    private static byte[] initKey()
            throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        return encrypt(data, name);
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        return decrypt(data, name);
    }

    public static String encryptToHex(String data) throws Exception {
        byte[] b = encrypt(data.getBytes(), name);
        return new String(Hex.encode(b));
    }

    public static String encryptToHex(String data, String charset) throws Exception {
        byte[] b = encrypt(data.getBytes(charset), name);
        return new String(Hex.encode(b), charset);
    }

    public static String decrypt4Hex(String data) throws Exception {
        byte[] b = Hex.decode(data);
        return new String(decrypt(b, name));
    }

    public static String decrypt4Hex(String data, String charset) throws Exception {
        byte[] b = Hex.decode(data);
        return new String(decrypt(b, name), charset);
    }

    public static String encryptToHex(String data, String charset, String key)
            throws Exception {
        byte[] b = encrypt(data.getBytes(charset), key.getBytes(charset));
        return new String(Hex.encode(b), charset);
    }

    public static String decrypt4Hex(String data, String charset, String key)
            throws Exception {
        byte[] b = Hex.decode(data);
        return new String(decrypt(b, key.getBytes(charset)), charset);
    }

    public static String encryptToBASE64(String data) throws Exception {
        byte[] b = encrypt(data.getBytes(), name);
        return new String(Base64.encode(b));
    }

    public static String encryptToBASE64(String data, String charset) throws Exception {
        byte[] b = encrypt(data.getBytes(charset), name);
        return new String(Base64.encode(b), charset);
    }

    public static String decrypt4BASE64(String data) throws Exception {
        byte[] b = Base64.decode(data);
        return new String(decrypt(b, name));
    }

    public static String decrypt4BASE64(String data, String charset) throws Exception {
        byte[] b = Base64.decode(data.getBytes(charset));
        return new String(decrypt(b, name), charset);
    }

    public static String encryptToBASE64(String data, String charset, String key) {
        try {
            byte[] b = encrypt(data.getBytes(charset), key.getBytes(charset));
            return new String(Base64.encode(b), charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt4BASE64(String data, String charset, String key) {
        try {
            byte[] b = Base64.decode(data.getBytes(charset));
            return new String(decrypt(b, key.getBytes(charset)), charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        // 0102030405060708
//        Random random = new Random();
//        long value = random.nextLong();
//        //String key = String.format("%016x", value);
//
//        String key = "9102030405060708";
//        System.out.println(key);
//
//        String data = "{\"pageSize\":10,\"rows\":63,\"result\":[{\"workStatus\":1,\"employeeName\":\"s*\",\"headerFlag\":0,\"creator\":\"张三\",\"userStatus\":1,\"mobile\":\"135****5214\",\"oid\":\"238426691029716992\",\"sort\":23,\"userName\":\"ad****2\",\"userCode\":\"0066\",\"createTime\":1597389796000,\"departId\":\"235809598374379520\",\"position\":\"2\",\"departName\":\"一级部门\"}]}";
//        //加密
//        String s = encryptToBASE64(data, "UTF-8", key);
//        System.out.println(s);
//        //解密
//        System.out.println(decrypt4BASE64(s, "UTF-8", key));
//
//    }
}
