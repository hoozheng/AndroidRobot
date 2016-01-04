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
参数:<br>
String pyPath - Python脚本路径 
String method - 方法名

#####  void logInfo(String log)
记录Log日志<br>
参数:String str - 日志文本<br>
返回值:true - 记录成功  false - 记录失败

#####  boolean longClick(int x, int y)
长按点击坐标<br>
参数:int x - x坐标<br>
int y - y坐标<br>
返回值:true - 点击成功    false - 点击失败

#####  boolean longClick(String text)
长按点击文本<br>
参数:String text - 文本文字<br>
返回值:true - 点击成功    false - 点击失败

#####  boolean pressBack()
模拟返回键<br>
返回值:true - 返回成功  false - 返回失败

#####  boolean pressDelete()
模拟删除按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressDPadCenter()
模拟按轨迹球中点按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressDPadDown()
模拟按轨迹球下点按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressDPadLeft()
模拟按轨迹球左点按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressDPadRight()
模拟按轨迹球右点按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressDPadUp()
模拟按轨迹球上点按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressEnter()
模拟回车按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressHome()
模拟HOME按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean pressKeyCode(int keyCode, int metaState)
发送KeyCode按键<br>
参数:
int keyCode - 键盘KeyCode值   int metaState - 大小写(1大写  0小写)<br>
返回值:true - 发送成功  false - 发送失败

#####  boolean pressKeyWords(String str)
根据字符发送KeyCode按键<br>
参数:String str - 需要输入的字符串<br>
返回值:true - 发送成功  false - 发送失败

#####  boolean pressMenu()
模拟MENU按键<br>
返回值:true - 模拟成功  false - 模拟失败

#####  boolean reboot()
重启设备<br>
返回值:true - 点击成功    false - 点击失败

#####  void registerWatcher(String name, String path, String func)  
注册Watcher监听<br>
返回值:void  

#####  void removeWatcher(String name)  
注销监听器<br>
参数:String name - 监听器名  

#####  void script(int width, int height)  
设置当前脚本对应的屏幕大小,在用到坐标的地方会自动进行不同屏幕适配  

#####  boolean scrollToBeginning(int maxSwipes)  
滚屏至屏幕底  

#####  boolean scrollToEnd(int maxSwipes)  
滚屏至屏幕顶  

#####  boolean setTextByClass(String object, int instance, String text)  
设置指定控件的文本<br>
参数:<br>String object - Class属性<br>int instance - 第n个<br>String text - 设置的文本  
返回值:true - 设置成功  false - 设置失败  

#####  boolean setTextById(String id, int instance, String text)  
设置指定控件的文本<br>
参数:
String id - 控件id属性<br>int instance - 第n个<br>String text - 设置的文本  
返回值:true - 设置成功  false - 设置失败  

#####  boolean startActivity(String activityName)  
根据Activity名字启动应用程序<br>
参数:String activityName - Activity名字<br>
返回值:true - 启动成功  false - 启动失败  

#####  boolean swipe(int startX, int startY, int endX, int endY)  
滑动屏幕<br>
参数:
int startX - 开始点x坐标、int startY - 开始点y坐标、int endX - 结束点x坐标、int endY - 结束点y坐标<br>
返回值:true - 滑动成功  false - 滑动失败  

#####  boolean findText(String text)  
查找UI是否包含文本<br>
参数:String text - 文本文字<br>
返回值:true - 查找成功    false - 查找失败  

#####  void triggerWatchers()  
触发已经注册的Watcher<br>

#####  void call(String num)  
拨打电话  

#####  void sendSMS(String num, String content)  
发送短线  

#####  String shell(String cmd, int timeout)  
执行adb shell脚本

#####  void sleep(long millisec)  
等待<br>
参数:long millisec - 等待时间(毫秒)  

#####  String takeSnapshot(String path, String fileName)  
获取屏幕快照<br>
参数:String path - 快照存储本地的路径、String fileName - 快照名<br>
返回值:String - 成功返回改快照在本地的路径

