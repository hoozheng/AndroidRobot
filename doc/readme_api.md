# AndroidRobot API 概述
AndroidRobot API分对Native控件支持的API和对Webview控件支持的API。<br>

### Native API
##### \*boolean click(int x, int y)
点击坐标<br>
参数: int x - x坐标 int y - y坐标<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  \*boolean click(String text)  
点击文本<br>
参数:String text - 文本文字<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  \*boolean clickAndWaitForNewWindow(String text, long timeout)
点击文本并等待新窗口<br>
参数:String text - 文本文字 long timeout - 等待超时<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  \*boolean clickByClass(String object, int instance)
点击控件属性<br>
参数:String object - 控件属性 int instance - 第n个控件<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  \*boolean clickById(String id, int instance)
点击控件id<br>
参数:String object - 控件属性 int instance - 第n个控件<br>
返回值:true - 点击成功    false - 点击失败<br>

#####  \*boolean compare(String filePath, int rate)
图片比较<br>
参数:String filePath - 原图相对路径 int rate - 相似度<br>
返回值:true - 比较成功  false - 比较失败

### WebView API
