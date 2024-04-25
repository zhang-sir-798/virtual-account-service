package cn.com.finance.ema.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>
 * sign工具类
 * </p>
 *
 * @author zhang_sir
 * @version v1.0.0
 * @since 2022/06/21 15:13
 */
@Slf4j
public class SignOlUtil {

	/**
	 * 加密算法：
	 * MD5算法 转base64编码。
	 *
	 * @param json
	 * @param macKey
	 * @return string
	 */
	public static String encrypt(String json, String macKey) {

		Map<String, String> contentData = JSON.parseObject(json, Map.class);

		String macStr = "";
		Object[] key_arr = contentData.keySet().toArray();
		Arrays.sort(key_arr);
		for (Object key : key_arr) {
			Object value = contentData.get(key);
			if (value != null) {
				if (!key.equals("sign")) {
					macStr += value.toString();
				}
			}
		}
		String rMac = "";
		try {
			rMac = sign_MD5(macStr, macKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rMac;
	}
	/**
	 * 验签：
	 * MD5算法 转base64编码
	 *
	 * @param reqStr
	 * @param key
	 * @return boolean
	 */
	public static boolean verify(String reqStr, String key) {

		JSONObject params = JSONObject.parseObject(reqStr);

		String sign = (String) params.get("sign"); // 签名
		String makeSign = "";
		params.remove("sign"); // 不参与签名
		try {

			Map<String, String> contentData = JSON.parseObject(params.toString(), Map.class);
			String macStr = "";
			Object[] key_arr = contentData.keySet().toArray();
			Arrays.sort(key_arr);
			for (Object keys : key_arr) {
				Object value = contentData.get(keys);
				if (value != null) {
					if (!keys.equals("sign")) {
						macStr += value.toString();
					}
				}
			}

			makeSign = sign_MD5(macStr, key);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!makeSign.equalsIgnoreCase(sign)) {
			return false;
		}
		return true;
	}

	public static String sign_MD5(String macStr, String mackey) throws Exception {
		String s = MD5Encode(macStr + mackey);
		BASE64Encoder base64en = new BASE64Encoder();
		String basestr = base64en.encode(s.getBytes("utf-8"));
		return basestr;
	}

	public static String MD5Encode(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(sourceStr.getBytes("UTF-8"));
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 报件MD5：
	 * MD5算法 
	 * @param s
	 * @return boolean
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes(Charset.forName("UTF-8"));
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
