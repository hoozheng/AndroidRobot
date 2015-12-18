# AndroidRobot API 概述
AndroidRobot API分对Native控件支持的API和对Webview控件支持的API。<br>

### Native API
#####  boolean click(int x, int y)
点击坐标<br>
参数: int x - x坐标 int y - y坐标<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  boolean click(String text)  
点击文本<br>
参数:String text - 文本文字<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  boolean clickAndWaitForNewWindow(String text, long timeout)
点击文本并等待新窗口<br>
参数:String text - 文本文字 long timeout - 等待超时<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  boolean clickByClass(String object, int instance)
点击控件属性<br>
参数:String object - 控件属性 int instance - 第n个控件<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  boolean clickById(String id, int instance)
点击控件id<br>
参数:String object - 控件属性 int instance - 第n个控件<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  boolean compare(String filePath, int rate)
图片比较<br>
参数:String filePath - 原图相对路径 int rate - 相似度<br>
返回值:true - 比较成功  false - 比较失败

#####  String getActivityName()
获得被测Apk的Activity名<br>

#####  String getSerialNumber()
获得被测手机的Serial Number\n\n返回值:被测手机的序列号<br>

#####  boolean install(String filePath)
安装应用程序<br>
参数:String filePath - app路径<br>
返回值:true - 安装成功  false - 安装失败<br>

#####  void invoke(String pyPath, String method, Object[] args)
调用自定义库函数<br>
参数:String pyPath - Python脚本路径 String method - 方法名

### WebView API
#####  boolean findElementByXpath(String xpath)
根据xpath查找目标<br>

##### String getPageSource()
获得WebView页面的Dom元素<br>


