
package cn.com.finance.ema.utils.channel;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * RSA 非对称、公钥加密,签名验签：基于数学函数
 * 公钥(n,e)加密: m^e=c(mod n)，m明文，c密文
 * 私钥(n,d)解密: c^d=m(mod n)，c密文，m明文
 */
public class RsaUtils {
    private static final Log logger = LogFactory.getLog(RsaUtils.class);

    private static final Map<String, RSAPrivateKey> privateKeyMap = MapUtil.newHashMap();
    private static final Map<String, RSAPublicKey> publicKeyMap = MapUtil.newHashMap();

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 算法常量
     */
    public static final String SIGN_ALGORITHM_SHA256RSA = "SHA256withRSA";

    /**
     * RSA Ecb模式 公钥加密
     *
     * @param publicKey 公钥
     * @param data      明文
     * @param padMode   填充模式
     * @return 密文
     */
    public static byte[] rsaEcbEncrypt(RSAPublicKey publicKey, byte[] data, String padMode) {
        //
        String algorithm = "RSA/ECB/" + padMode;
        byte[] res = null;
        if (publicKey == null) {
            logger.error("publicKey is null");
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            res = cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("Fail: RSA Ecb Encrypt", e);
        }
        return res;
    }

    /**
     * RSA Ecb 私钥解密
     *
     * @param privateKey 私钥
     * @param data       密文
     * @param padMode    填充模式
     * @return 明文
     */
    public static byte[] rsaEcbDecrypt(RSAPrivateKey privateKey, byte[] data, String padMode) {
        if (privateKey == null) {
            logger.error("privateKey is null");
        }
        String algorithm = "RSA/ECB/" + padMode;
        byte[] res = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            res = cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("Fail: RSA Ecb Decrypt", e);
        }
        return res;
    }

    /**
     * RSA摘要  私钥签名
     *
     * @param privateKey 私钥
     * @param data       消息
     * @return 签名
     */
    public static byte[] signRsa(RSAPrivateKey privateKey, byte[] data) {
        byte[] result = null;
        Signature st;
        try {
            st = Signature.getInstance(KEY_ALGORITHM);
            st.initSign(privateKey);
            st.update(data);
            result = st.sign();
        } catch (Exception e) {
            logger.error("Fail: RSA  sign", e);
        }
        return result;
    }

    /**
     * RSA Sha256摘要  私钥签名
     *
     * @param privateKey 私钥
     * @param data       消息
     * @return 签名
     */
    public static byte[] signWithSha256(RSAPrivateKey privateKey, byte[] data) {
        byte[] result = null;
        Signature st;
        try {
            st = Signature.getInstance(SIGN_ALGORITHM_SHA256RSA);
            st.initSign(privateKey);
            st.update(data);
            result = st.sign();
        } catch (Exception e) {
            logger.error("Fail: RSA with sha256 sign", e);
        }
        return result;
    }

    /**
     * RSA Sha256摘要  公钥验签
     *
     * @param publicKey 公钥
     * @param data      消息
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean verifyWithSha256(RSAPublicKey publicKey, byte[] data, byte[] sign) {
        boolean correct = false;
        try {
            Signature st = Signature.getInstance(SIGN_ALGORITHM_SHA256RSA);
            st.initVerify(publicKey);
            st.update(data);
            correct = st.verify(sign);
        } catch (Exception e) {
            logger.error("Fail: RSA with sha256 verify", e);
            return false;
        }
        return correct;
    }

    /**
     * RSA 摘要  公钥验签
     *
     * @param publicKey 公钥
     * @param data      消息
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean verifyRsa(RSAPublicKey publicKey, byte[] data, byte[] sign) {
        boolean correct = false;
        try {
            Signature st = Signature.getInstance(KEY_ALGORITHM);
            st.initVerify(publicKey);
            st.update(data);
            correct = st.verify(sign);
        } catch (Exception e) {
            logger.error("Fail: RSA verify", e);
        }
        return correct;
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String md5Sign(Map param, String key) throws Exception {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.putAll(param);
        treeMap.remove("digest");
        String signStr = getParamStr(treeMap);
        logger.info("MD5加密源串:" + signStr + key);
        String signature = MD5(signStr + key);
        logger.info("MD5加密后值:" + signature);
        return signature;
    }

    public static String sign(Map param, String key, String signType) {
        RSAPrivateKey privateKey = null;
        byte[] signature = null;
        try {

            privateKey = privateKeyMap.get("qbs:privateKey:" + param.get("merchantNo"));

            if (privateKey == null) {
                privateKey = (RSAPrivateKey) getPrivateKey(key);
                privateKeyMap.put("qbs:privateKey:" + param.get("merchantNo"), privateKey);
            }

            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.putAll(param);
            treeMap.remove("digest");
            String signStr = getParamStr(treeMap);
            System.out.println("signStr:" + signStr);
            byte[] msg = signStr.getBytes("UTF-8");

            if (SignType.RSA.getCode().equals(signType)) {
                signature = signRsa(privateKey, msg);
            } else if (SignType.RSA2.getCode().equals(signType)) {
                signature = signWithSha256(privateKey, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(signature);
    }

    public static boolean verify(Map param, String key, String signType, String sign) {
        RSAPublicKey publicKey = null;
        try {

            publicKey = publicKeyMap.get("qbs:publicKey:" + param.get("merchantNo"));

            if (publicKey == null) {
                publicKey = (RSAPublicKey) getPublicKey(key);
                publicKeyMap.put("qbs:publicKey:" + param.get("merchantNo"), publicKey);
            }

            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.putAll(param);
            treeMap.remove("digest");
            String signStr = getParamStr(treeMap);
            byte[] msg = signStr.getBytes("UTF-8");
            byte[] signB = Base64.decodeBase64(sign);
            if (SignType.RSA.getCode().equals(signType)) {
                return verifyRsa(publicKey, msg, signB);
            } else if (SignType.RSA2.getCode().equals(signType)) {
                return verifyWithSha256(publicKey, msg, signB);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static String getParamStr(TreeMap<String, String> paramsMap) {
        StringBuilder param = new StringBuilder();
        for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Map.Entry<String, String> e = it.next();
            if (StrUtil.isBlank(e.getValue())) {
                continue;
            }
            param.append(e.getKey()).append("=")
                    .append(e.getValue()).append("&");
        }
        return param.toString().substring(0, param.toString().length() - 1);
    }

    private static PrivateKey getPrivateKeyFromFile(String path) {
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);
            return priKey;
        } catch (IOException e) {
            logger.error("读取私钥文件失败！", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("获取私钥失败！", e);
        } catch (InvalidKeySpecException e) {
            logger.error("获取私钥失败！", e);
        }
        return null;
    }

    public static PublicKey getPublicKeyFromFile(String path) {
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pubX509);
        } catch (IOException e) {
            logger.error("读取公钥文件失败！", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("获取公钥失败！", e);
        } catch (InvalidKeySpecException e) {
            logger.error("获取公钥失败！", e);
        }
        return null;

    }

    private static String getkeyStrFromFile(String path) {
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
//					sb.append('\r');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            logger.error("读取私钥文件失败！", e);
        }
        return null;
    }
}
