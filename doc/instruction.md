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

### 运行任务