package com.android.chromedriver;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebElement;
import com.google.common.collect.ImmutableMap;

public class ChromeDriverClient {
private WebDriver driver = null;
    
    public void createDriver(String pkg_name, String sn) {
    	if(this.driver == null) {
	    	System.out.println(sn + "================createDriver======================");
	        ChromeOptions chromeOptions = new ChromeOptions();
	        chromeOptions.setExperimentalOption("androidPackage", pkg_name);
	//        chromeOptions.setExperimentalOption("androidActivity", "com.eg.android.AlipayGphone.AlipayLogin");
	//        chromeOptions.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
	        chromeOptions.setExperimentalOption("androidUseRunningApp", true);
	        chromeOptions.setExperimentalOption("androidDeviceSerial", sn);
	//        Map<String, Object> chromeOptions = new HashMap<String, Object>();
	//        chromeOptions.put("androidPackage", "com.eg.android.AlipayGphoneRC");
	//        chromeOptions.put("androidActivity", "com.eg.android.AlipayGphone.AlipayLogin");
	        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	        LoggingPreferences logPrefs = new LoggingPreferences();
	        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
	        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
	        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//	        capabilities.setCapability(CapabilityType., value);
	        if(ChromeService.getService() != null)
	        	driver = new RobotRemoteWebDriver(ChromeService.getService().getUrl(), capabilities);
	        
	        int size = driver.getWindowHandles().size();
	        System.out.println("size:" + size);
	        for(int i=0;i<size;i++) {
	        	System.out.println(driver.getWindowHandles().toString());
	        }
    	}
    }
    
	public boolean tapById(String id) {
		try{
			Map<String, ?> params = ImmutableMap.of("element", ((RemoteWebElement)driver.findElement(By.id(id))).getId());
			((RobotRemoteWebDriver)this.driver).execute(DriverCommand.TOUCH_SINGLE_TAP, params);
		}catch(Exception ex) {
			return false;
		}
		return true;
	}
	
	public boolean tapByXPath(String xpath) {
		try {
			Map<String, ?> params = ImmutableMap.of("element", ((RemoteWebElement)driver.findElement(By.xpath(xpath))).getId());
			((RobotRemoteWebDriver)this.driver).execute(DriverCommand.TOUCH_SINGLE_TAP, params);
		}catch(Exception ex) {
			return false;
		}
		return true;
	}
	
	public boolean tap(By by) {
		try {
			Map<String, ?> params = ImmutableMap.of("element", ((RemoteWebElement)driver.findElement(by)).getId());
			((RobotRemoteWebDriver)this.driver).execute(DriverCommand.TOUCH_SINGLE_TAP, params);
		}catch(Exception ex) {
			return false;
		}
		return true;
	}
    
    public void quitDriver() {
    	if(driver != null)
    		driver.quit();
    }
    
    public WebDriver getDriver() {
    	return this.driver;
    }
    
    public WebElement findElement(By by) {
    	try{
    		return this.driver.findElement(by);
    	}catch(Exception ex) {
    		return null;
    	}
    }
    
    public List<WebElement> findElements(By by) {
    	return this.driver.findElements(by);
    }
    
    public String getpageSource() {
    	return this.driver.getPageSource();
    }
    
	public boolean sendKeys(By by, String str) {
		WebElement element3 = findElement(by);
		if(element3 == null)
			return false;
		element3.sendKeys(str);
		return true;
	}
    
}
