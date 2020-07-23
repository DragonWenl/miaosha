package com.lwl.miaosha.redis;

public class AccessKey extends BasePrefix{

	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	//动态设置有效期
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}

	public static KeyPrefix num = new AccessKey(5,"access");
}
