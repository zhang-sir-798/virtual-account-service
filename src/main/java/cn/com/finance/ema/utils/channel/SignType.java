/**
 * @title 		MAS(Merchant Acquire System)
 * @description	商户收单系统
 * @usage		
 * @copyright	Copyright 2011 coolcoon Corporation. All rights reserved.
 * @company		Coolcoon Corporation.
 * @author		admin
 * @version		$Id: SignType.java,v 1.0 2011-2-18 下午01:10:28 admin Exp $
 * @create		2011-2-18 下午01:10:28
 */
package cn.com.finance.ema.utils.channel;

/**
 * 签名类型
 */
public enum SignType {
    
	RSA("RSA", "RSA"),RSA2("RSA2","RSA2"), MD5("MD5", "MD5"), CA("CA", "CA"),PKI("PKI","PKI"),
	DEFAULT("MD5","MD5");
	
	private final String code;
	private final String name;

	SignType(String code, String name) {
		this.name = name;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static SignType getByCode(String code) {
		for (SignType signType : SignType.values()) {
			if (signType.getCode().equals(code)) {
				return signType;
			}
		}
		return null;
	}
}
