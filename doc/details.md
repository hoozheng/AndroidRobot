# 如何运行用例
如何运行我的第一个用例来完成自动化测试

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
