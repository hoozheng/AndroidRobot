package com.android.util;

/**
 * @author Geek_Soledad (66704238@51uc.com)
 */
public class ImpliedFeature {

	/**
	 * 要的设备特性名称。
	 */
	private String feature;

	/**
	 * 表明所需特性的内容。
	 */
	private String implied;

	public ImpliedFeature() {
		super();
	}

	public ImpliedFeature(String feature, String implied) {
		super();
		this.feature = feature;
		this.implied = implied;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getImplied() {
		return implied;
	}

	public void setImplied(String implied) {
		this.implied = implied;
	}

	@Override
	public String toString() {
		return "Feature [feature=" + feature + ", implied=" + implied + "]";
	}
}