package com.android.selendroid;

import io.selendroid.common.SelendroidCapabilities;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class MyTest {

	//public static final String TEST_APP_ID = "io.selendroid.testapp:0.15.0";
	public static final String TEST_APP_ID = "com.eg.android.AlipayGphoneRC:8.6.0.032901";

	public void test() {
		SelendroidCapabilities capa = new SelendroidCapabilities(TEST_APP_ID);
		capa.setLaunchActivity("com.eg.android.AlipayGphone.AlipayLogin");
		WebDriver driver = new RemoteWebDriver(capa);
		//driver.switchTo().window("NATIVE_APP");//
		WebElement inputField = driver.findElement(By.id("buttonStartWebview"));
		inputField.click();
		
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		System.out.println("============================================");
		WebDriver webDriver = driver.switchTo().window("WEBVIEW_0");
		System.out.println("=====================3432=======================");
		webDriver.manage().timeouts().implicitlyWait(5000, TimeUnit.SECONDS);
		System.out.println("getWindowHandle:" + webDriver.getPageSource());
		//WebElement element3 = webDriver.findElement(By.xpath("//input[@value='Send me your name!']"));
		WebElement element3 = webDriver.findElement(By.xpath("//input[@id='name_input']"));
		element3.sendKeys("he zheng");
		//element3.clear();

		webDriver.manage().timeouts().implicitlyWait(5000, TimeUnit.SECONDS);
//		inputField.sendKeys("Selendroid");
//		Assert.assertEquals("Selendroid", inputField.getText());
		//driver.quit();
	}
	
	public static void main(String[] args) {
		new MyTest().test();
	}
	
}
