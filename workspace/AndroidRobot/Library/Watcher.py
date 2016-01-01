#_*_ coding: iso8859_1
# Script API

from com.android.python import AndroidDriver

def isUnExpectedWindow():
    sn = device[0].getSerialNumber()   
    device[0].sleep(1000)        
    
def watcher():
    isUnExpectedWindow()
