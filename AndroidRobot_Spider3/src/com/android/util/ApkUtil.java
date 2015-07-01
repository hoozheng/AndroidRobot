package com.android.util;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApkUtil {
	public static final String VERSION_CODE = "versionCode";
	public static final String VERSION_NAME = "versionName";
	public static final String SDK_VERSION = "sdkVersion";
	public static final String TARGET_SDK_VERSION = "targetSdkVersion";
	public static final String USES_PERMISSION = "uses-permission";
	public static final String APPLICATION_LABEL = "application-label";
	public static final String APPLICATION_ICON = "application-icon";
	public static final String USES_FEATURE = "uses-feature";
	public static final String USES_IMPLIED_FEATURE = "uses-implied-feature";
	public static final String SUPPORTS_SCREENS = "supports-screens";
	public static final String SUPPORTS_ANY_DENSITY = "supports-any-density";
	public static final String DENSITIES = "densities";
	public static final String PACKAGE = "package";
	public static final String APPLICATION = "application:";
	public static final String LAUNCHABLE_ACTIVITY_NAME = "launchable activity name";

	private ProcessBuilder mBuilder;
	private static final String SPLIT_REGEX = "(: )|(=')|(' )|'";
	private static final String FEATURE_SPLIT_REGEX = "(:')|(',')|'";

	public ApkUtil() {
		mBuilder = new ProcessBuilder();
		mBuilder.redirectErrorStream(true);
	}

	public ApkInfo getApkInfo(String apkPath) throws Exception {
		Process process = mBuilder.command("./aapt", "d", "badging", apkPath)
				.start();
		InputStream is = null;
		is = process.getInputStream();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(is, "utf8"));
		String tmp = br.readLine();
		try {
			if (tmp == null || !tmp.startsWith("package")) {
				throw new Exception("参数不正确，无法正常解析APK包。输出结果为:" + tmp + "...");
			}
			ApkInfo apkInfo = new ApkInfo();
			do {
				setApkInfoProperty(apkInfo, tmp);
			} while ((tmp = br.readLine()) != null);
			return apkInfo;
		} catch (Exception e) {
			throw e;
		} finally {
			process.destroy();
			closeIO(is);
			closeIO(br);
		}
	}

	private void setApkInfoProperty(ApkInfo apkInfo, String source) {
		if (source.startsWith(PACKAGE)) {
			splitPackageInfo(apkInfo, source);
		} else if (source.startsWith(SDK_VERSION)) {
			apkInfo.setSdkVersion(getPropertyInQuote(source));
		} else if (source.startsWith(TARGET_SDK_VERSION)) {
			apkInfo.setTargetSdkVersion(getPropertyInQuote(source));
		} else if (source.startsWith(USES_PERMISSION)) {
			apkInfo.addToUsesPermissions(getPropertyInQuote(source));
		} else if (source.startsWith(APPLICATION_LABEL)) {
			apkInfo.setApplicationLable(getPropertyInQuote(source));
		} else if (source.startsWith(APPLICATION_ICON)) {
			apkInfo.addToApplicationIcons(getKeyBeforeColon(source),
					getPropertyInQuote(source));
		} else if (source.startsWith(APPLICATION)) {
			String[] rs = source.split("( icon=')|'");
			apkInfo.setApplicationIcon(rs[rs.length - 1]);
		} else if (source.startsWith(USES_FEATURE)) {
			apkInfo.addToFeatures(getPropertyInQuote(source));
		} else if (source.startsWith(USES_IMPLIED_FEATURE)) {
			apkInfo.addToImpliedFeatures(getFeature(source));
		} else if(source.startsWith(LAUNCHABLE_ACTIVITY_NAME)){
			apkInfo.setActivityName(source.split(SPLIT_REGEX)[1]);
		}
	}

	private ImpliedFeature getFeature(String source) {
		String[] result = source.split(FEATURE_SPLIT_REGEX);
		ImpliedFeature impliedFeature = new ImpliedFeature(result[1], result[2]);
		return impliedFeature;
	}

	private String getPropertyInQuote(String source) {
		return source.substring(source.indexOf("'") + 1, source.length() - 1);
	}

	private String getKeyBeforeColon(String source) {
		return source.substring(0, source.indexOf(':'));
	}

	private void splitPackageInfo(ApkInfo apkInfo, String packageSource) {
		String[] packageInfo = packageSource.split(SPLIT_REGEX);
		apkInfo.setPackageName(packageInfo[2]);
		apkInfo.setVersionCode(packageInfo[4]);
		apkInfo.setVersionName(packageInfo[6]);
	}

	private final void closeIO(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			String demo = "d:/zhizhi_v3.0.apk";
			if (args.length > 0) {
				demo = args[0];
			}
			ApkInfo apkInfo = new ApkUtil().getApkInfo(demo);
			System.out.println(apkInfo.getActivityName() + " " + apkInfo.getPackageName() + " " + apkInfo.getVersionName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
