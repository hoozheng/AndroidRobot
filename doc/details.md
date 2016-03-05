# 如何运行用例
如何运行我的第一个用例来完成自动化测试

### 设置被测APK
![setdut](https://github.com/hoozheng/AndroidRobot/blob/master/doc/setDUT.png)  
1,设置被测APK信息后，AndroidRobot会获得改APK的基本信息，例如Package、Activity  
2,如果指定强制安装，AndroidRobot会强制安装指定的APK版本。  
3,目前对Seledroid的支持不是很好，请用户不要打钩使用，Selendroid需要用到签名，算是修改了被测Apk的包。  
4,Chromedriver模式是Google对webview的自动化一种创新，目前应用比较广泛，主要缺点是只支持Android 4.4.2以及以上系统。  

### 创建脚本
1,创建一个新脚本
![new](https://github.com/hoozheng/AndroidRobot/blob/master/doc/new.png)

2,编辑脚本如下
脚本内容：
\#_*_ coding: iso8859_1  
\# Script API  
  
 _from com.android.python import AndroidDriver_  
 _from org.openqa.selenium import By_  
 
 def test():  
 &nbsp;&nbsp;&nbsp;&nbsp;device[0].logInfo('This is Hello World!') \#Log中记录了一行文字  
      
      
if \_\_name\_\_ == '\_\_main\_\_':  
&nbsp;&nbsp;&nbsp;&nbsp;test()

### 添加设备
选择待添加设备
![newdevice](https://github.com/hoozheng/AndroidRobot/blob/master/doc/adddevice.png)  

添加成功
![add](https://github.com/hoozheng/AndroidRobot/blob/master/doc/add.png)  

### 创建任务
新建任务
![newtask](https://github.com/hoozheng/AndroidRobot/blob/master/doc/newtask.png)  

输入任务名test
![newtest](https://github.com/hoozheng/AndroidRobot/blob/master/doc/new_test.png)

选择脚本并保存
![save](https://github.com/hoozheng/AndroidRobot/blob/master/doc/save_test.png)

### 运行任务
运行任务
* **并发运行**
  任务以独立的方式在每个手机上运行，每个手机之间不能通过脚本交互。这种运行模式适合批量运行同一任务，不需要多手机进行交互。
* **交互运行**
  脚本在每个手机上是交互运行的，控制不同的手机使用不同的设备号，device[0]、device[1]...device[n]。这种运行模式适合需要交互的场景，
比如相互发送短信、打电话、接电话、多方通话、MTBF测试等等，一般运营商测试用的比较多。

![run](https://github.com/hoozheng/AndroidRobot/blob/master/doc/run.png)
