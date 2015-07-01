#_*_ coding: iso8859_1
# Script API
# 版权所有 @迈测科技

from com.android.python import AndroidDriver


device[0].script(720, 1280)

def login():
    device[0].logInfo('WebView Test')
    device[0].sleep(1000)
    device[0].pressKeyWords('!')
    #device[0].click('移动办公')
    
if __name__ == '__main__':
    login()
