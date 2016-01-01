package com.android.chromedriver;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

public class RobotRemoteWebDriver extends RemoteWebDriver{
	
	public RobotRemoteWebDriver(URL remoteAddress, Capabilities desiredCapabilities) {
		super(remoteAddress, desiredCapabilities);
	}
	
	public Response execute(String driverCommand, Map<String, ?> parameters) {
		return super.execute(driverCommand, parameters);
	}
}
