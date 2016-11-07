package com.bean.business;

import com.bean.BaseBean;

/** 证书的公私钥 */
public class PassBean extends BaseBean {
	private static final long serialVersionUID = 1101389407320519877L;
	/** 公钥 */
	private String publicKey;
	/** 私钥 */
	private String privateKey;
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
