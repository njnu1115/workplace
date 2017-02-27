# Study
这个目录下面目前有两个Android Studio的项目,之所以这样是因为Github免费只有三个项目,所以我就把多个项目挤在一个git里面了。

CallTorrent这个名字反映的是这个项目最初的目标：手机无论在哪，只要有网，就能把电话短信转发到希望的目的地，就好像BT网络一样，去中心化。但是目前只做到了短信的转发。

git clone下去之后，打开android studio, import, build, 出来apk之后装到手机上。然后有下面这些坑需要注意：

首先，设置WLAN 策略为 always on(一直连接)或者only when plugin(仅限充电时)。always on的话电池比较受罪。only when plugin的话，需要一直插着充电器。有的人对于一直插充电器这件事很反感：）

然后，打开App，设置几个参数。主要就是发送邮件的参数：smtp的服务器，用户名，密码，to address, 等等。建议先用foxmail验证一下这些参数是否好使。拿QQ邮箱举例，现在对smtp发信管理非常严格，需要生成一次性的密码。

to address最好设置一个QQ邮箱，然后用微信打开QQ邮箱提醒功能，就OK啦。

如果以上都关注到了，还是不能收到，那在Android Studio里面看看logcat吧。

个人精力有限，只能帮到这里拉。

哦，对了，为什么不用现成的App，非要自己写一个？你说呢？短信这么重要的信息，当然是用开源能看得到代码的工具放心咯。
