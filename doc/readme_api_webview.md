# AndroidRobot API 概述
AndroidRobot API对Webview控件支持的API。<br>

### WebView API
#####  boolean findElementByXpath(String xpath)
根据xpath查找目标<br>

#####  String getPageSource()
获得WebView页面的Dom元素<br>

#####  void sendKeysByXpath(String xpath, String text)  
设置指定控件的文本<br>
参数:String xpath - 控件xpath属性<br>String text - 设置的文本<br>
返回值:true - 设置成功  false - 设置失败  

#####  boolean startURL(String url)  
根据URL名字启动应用程序,主要用于HTML5应用<br>
参数:String url - url名字<br>
返回值:true - 启动成功  false - 启动失败  

#####  boolean Tap(By)  
点击控件  

#####  boolean tapByXpath(String xpath)  
按Xpath点击控件  
