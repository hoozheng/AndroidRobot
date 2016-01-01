package com.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApkInfo {
	public static final String APPLICATION_ICON_120 = "application-icon-120";
	public static final String APPLICATION_ICON_160 = "application-icon-160";
	public static final String APPLICATION_ICON_240 = "application-icon-240";
	public static final String APPLICATION_ICON_320 = "application-icon-320";
	/**
	 * apk内部版本号
	 */
	private String versionCode = null;
	/**
	 * apk外部版本号
	 */
	private String versionName = null;
	/**
	 * apk的包名
	 */
	private String packageName = null;
	/**
	 * 支持的android平台最低版本号
	 */
	private String minSdkVersion = null;
	/**
	 * apk所需要的权限
	 */
	private List<String> usesPermissions = null;

	/**
	 * 支持的SDK版本。
	 */
	private String sdkVersion;
	/**
	 * 建议的SDK版本
	 */
	private String targetSdkVersion;
	/**
	 * 应用程序名
	 */
	private String applicationLable;
	/**
	 * 各个分辨率下的图标的路径。
	 */
	private Map<String, String> applicationIcons;

	/**
	 * 程序的图标。
	 */
	private String applicationIcon;
	

	/**
	 * 暗指的特性。
	 */
	private List<ImpliedFeature> impliedFeatures;

	/**
	 * 所需设备特性。
	 */
	private List<String> features;
	

	private String activityName;

	public ApkInfo() {
		this.usesPermissions = new ArrayList<String>();
		this.applicationIcons = new HashMap<String, String>();
		this.impliedFeatures = new ArrayList<ImpliedFeature>();
		this.features = new ArrayList<String>();
	}

	/**
	 * 返回版本代码。
	 * 
	 * @return 版本代码。
	 */
	public String getVersionCode() {
		return versionCode;
	}

	/**
	 * @param versionCode
	 *            the versionCode to set
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	/**
	 * 返回版本名称。
	 * 
	 * @return 版本名称。
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * @param versionName
	 *            the versionName to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	/**
	 * 返回支持的最小sdk平台版本。
	 * 
	 * @return the minSdkVersion
	 */
	public String getMinSdkVersion() {
		return minSdkVersion;
	}

	/**
	 * @param minSdkVersion
	 *            the minSdkVersion to set
	 */
	public void setMinSdkVersion(String minSdkVersion) {
		this.minSdkVersion = minSdkVersion;
	}

	/**
	 * 返回包名。
	 * 
	 * @return 返回的包名。
	 */
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * 返回sdk平台版本。
	 * 
	 * @return
	 */
	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	/**
	 * 返回所建议的SDK版本。
	 * 
	 * @return
	 */
	public String getTargetSdkVersion() {
		return targetSdkVersion;
	}

	public void setTargetSdkVersion(String targetSdkVersion) {
		this.targetSdkVersion = targetSdkVersion;
	}

	/**
	 * 返回所需的用户权限。
	 * 
	 * @return
	 */
	public List<String> getUsesPermissions() {
		return usesPermissions;
	}

	public void setUsesPermissions(List<String> usesPermission) {
		this.usesPermissions = usesPermission;
	}

	public void addToUsesPermissions(String usesPermission) {
		this.usesPermissions.add(usesPermission);
	}

	/**
	 * 返回程序的名称标签。
	 * 
	 * @return
	 */
	public String getApplicationLable() {
		return applicationLable;
	}

	public void setApplicationLable(String applicationLable) {
		this.applicationLable = applicationLable;
	}

	/**
	 * 返回应用程序的图标。
	 * 
	 * @return
	 */
	public String getApplicationIcon() {
		return applicationIcon;
	}

	public void setApplicationIcon(String applicationIcon) {
		this.applicationIcon = applicationIcon;
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * 返回应用程序各个分辨率下的图标。
	 * 
	 * @return
	 */
	public Map<String, String> getApplicationIcons() {
		return applicationIcons;
	}

	public void setApplicationIcons(Map<String, String> applicationIcons) {
		this.applicationIcons = applicationIcons;
	}

	public void addToApplicationIcons(String key, String value) {
		this.applicationIcons.put(key, value);
	}

	public void addToImpliedFeatures(ImpliedFeature impliedFeature) {
		this.impliedFeatures.add(impliedFeature);
	}

	/**
	 * 返回应用程序所需的暗指的特性。
	 * 
	 * @return
	 */
	public List<ImpliedFeature> getImpliedFeatures() {
		return impliedFeatures;
	}

	public void setImpliedFeatures(List<ImpliedFeature> impliedFeatures) {
		this.impliedFeatures = impliedFeatures;
	}

	/**
	 * 返回应用程序所需的特性。
	 * 
	 * @return
	 */
	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public void addToFeatures(String feature) {
		this.features.add(feature);
	}
	
	public String getActivityName() {
		return this.activityName;
	}

	@Override
	public String toString() {
		return "ApkInfo [versionCode=" + versionCode + ",\n versionName="
				+ versionName + ",\n packageName=" + packageName
				+ ",\n minSdkVersion=" + minSdkVersion + ",\n usesPermissions="
				+ usesPermissions + ",\n sdkVersion=" + sdkVersion
				+ ",\n targetSdkVersion=" + targetSdkVersion
				+ ",\n applicationLable=" + applicationLable
				+ ",\n applicationIcons=" + applicationIcons
				+ ",\n applicationIcon=" + applicationIcon
				+ ",\n impliedFeatures=" + impliedFeatures + ",\n features="
				+ features + "]";
	}

}
