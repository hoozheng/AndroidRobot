#_*_ coding: iso8859_1
# Script API

from com.android.python import AndroidDriver
from org.openqa.selenium import By

def test():
    device[0].logInfo('This is Hello World!')
    device[0].sendSMS('13564590958', '你好')
    device[0].pressDPadRight()
    device[0].pressEnter()
    
    
    
    #device[0].getWebDriver().findElement(By.xpath("//input[@name='hphm']")).sendKeys("1234")
    #device[0].install('ffdasfda.apk')
    
if __name__ == '__main__':
    test()
