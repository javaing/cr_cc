package com.aliee.quei.mo.utils;

/**
 * 编码类型
 */
public enum Charset {
	UTF8("UTF-8"),
	UTF16LE("UTF-16LE"),
	UTF16BE("UTF-16BE"),
	GBK("GBK");
	
	private final String mName;
	public static final byte BLANK = 0x0a;

	Charset(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
}
