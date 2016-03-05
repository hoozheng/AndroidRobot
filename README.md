# AndroidRobot
### 介绍
AndroidRobot是专门为Android设计的一款自动化测试工具，采用Python脚本驱动，支持针对应用或者针对设备的测试自动化，对于应用级别他很完美的支持了Native、Webview的自动化，对于设备级别的能够很好的支持重启重连设备、交互、顺序等执行方式。他适合企业级别的APP测试，也适合运营商类型的MTBF测试，以开源的方式，提供企业级的自动化方案。</br>
目前支持以下功能：</br>
1，	清晰简洁的IDE界面风格</br>
2，	支持Python脚本</br>
3，	支持脚本编写、控件的识别、录制</br>
4，	支持Native、Webview控件识别</br>
5，	具有图像比较功能</br>
6，	具有任务管理功能、流畅的自动化引擎、清晰的输出报告</br>

### 支持平台
* Android

### 环境配置
* [Eclipse Luna Service Release 2 (4.4.2)](http://www.eclipse.org/downloads/packages/release/Luna/SR2)
* [SWT plugin](http://archive.eclipse.org/eclipse/downloads/drops4/R-4.4.2-201502041700/index.php#SWT)
* [JDK 1.8 x64](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### 快速入门
AndroidRobot是用Python脚本来驱动的，一个灵活性的开源框架。测试的一开始先建一个脚本，然后根据IDE的提示，编写脚本或者通过坐标录制脚本，脚本的主入口是Python的main函数。运行的时候需要在任务管理模块新建一个任务，通过任务关联脚本，我们只需要运行这个任务，可以很顺利的编辑运行自己的各种用例的组合。

### 原理介绍
AndroidRobot采用Python语言为驱动脚本，通过AndroidRobot框架分发用户的命令至手机端去执行。如果是Native的API，框架会自动分发命令到[UiAutomator](http://android.toolib.net/tools/help/uiautomator/index.html)。如果是webview的API，Android4.4.2系统及以上我们会采用[Chromedriver](https://sites.google.com/a/chromium.org/chromedriver/getting-started/getting-started---android)来驱动手机，Android4.4.2以下系统采用[Selendroid](http://selendroid.io/)。


### 架构图
![Architecture](https://github.com/hoozheng/AndroidRobot/blob/master/architecture.PNG)

### 下载
* [AndroidRobot Spider 3.0 x32]()
* [AndroidRobot Spider 3.0 x64](https://github.com/hoozheng/BinaryCode/AndroidRobot_Spider3.0x64.zip)
* [AndroidRobot Spider 3.0 mac]()
  
  
### [AndroidRobot API]  
AndroidRobot脚本支持的API详细描述，用户可以根据自己的需求定制自己的API  

##### [Native API](/doc/readme_api.md)  
##### [WebView API](/doc/readme_api_webview.md)
    
### [如何运行](/doc/instruction.md)
1,如何写一个Hello Wrold
2,如何创建任务
3,如何运行自己的第一个用例  


### [详细使用文档](/doc/details.md)
1,AndroidRobot详细使用说明

### 里程碑
2014.01 - 2015.12 完成第一版本  
2016.01 - 2016.02 完成对mac机器的运行  
2016.02 - 2016.05 对iphone自动化的支持  

### Contributor
何政(架构与开发)、陈能技(运营合作)

### Contacts
* hoozheng@126.com
* QQ:356850522


<a>                                              分享是一种情怀</a>

