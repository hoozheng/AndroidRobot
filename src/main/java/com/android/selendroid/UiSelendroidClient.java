package com.android.selendroid;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.selendroid.common.SelendroidCapabilities;


public class UiSelendroidClient {
	private String sn = "";
	private int port = 0;
	private String app = "";
	private WebDriver driver = null;
	
	public UiSelendroidClient(String app, String sn, int port){
		this.sn = sn;
		this.port = port;
		this.app = app;
	}
	
	//"com.eg.android.AlipayGphoneRC:8.6.0.032901"
	//"com.eg.android.AlipayGphone.AlipayLogin"
	public boolean setup(String serial, String pkgName, String actName, String version) throws Exception{
		SelendroidCapabilities capa = new SelendroidCapabilities(pkgName+":"+version);
		capa.setLaunchActivity(actName);
		capa.setSerial(serial);
		driver = new RemoteWebDriver(capa);
		return true;
	}
	
	public void switchTo(String content) {
		this.driver.switchTo().window(content);
	}
	
	public String getPageSource() {
		return this.driver.getPageSource();
	}
	
	
	public WebElement findElement(String xPath) {
		System.out.println("xpath:" + xPath);
		return this.driver.findElement(By.xpath(xPath));
	}
	
	public boolean sendKeys(String xPath, String str) {
		WebElement element3 = this.driver.findElement(By.xpath(xPath));
		element3.sendKeys(str);
		return true;
	}
	
	public void quit() {
		if(this.driver != null)
			this.driver.quit();
	}
}