#这段代码的用途是从知乎日报的URL下载全文，然后用beautifulsoap4来分析其中的question和answer两段，摘下来之后输出。然后可以用Calibre做成电子书

from bs4 import BeautifulSoup
import urllib2

#url.txt是用另一个脚本处理得到的知乎日报的URL，类似于 http://daily.zhihu.com/story/2980577
f=open('url.txt')
while 1:
	#
	dailyURL = f.readline()
	if dailyURL:
		#把URL打印到屏幕上校对
		print dailyURL
		#准备下载
		myRequest = urllib2.Request(dailyURL)
		#如果不改掉UA的话会被知乎网站屏蔽
		myRequest.add_header('User-Agent', 'Mozilla/5.0 (MI-ONE Plus)AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1')
		#下载内容
		dailycontent = urllib2.urlopen(myRequest) 
		#用BeautifulSoap处理
		myBS = BeautifulSoup(dailycontent)
		#转码GBK
		#print(myBS.prettify("gbk"))
		#把Question和Answer两段打印出来，其他都不要了
		print(myBS.find_all('div','question'))
		print(myBS.find_all('div','answer'))
	else:
		break
		
#print(myBS.find(class_="main-wrap"))
#print(myBS.prettify("gbk"))
#print(myBS.body)
#for child in myBS.body.descendants:
#    print(child)
