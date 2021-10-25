打算从今天开始写一个《我要转行干码农》系列

今天是第一篇，讲讲
# 怎么准备电脑

找一台电脑，不用太新不用太强，旧电脑翻出来，清清灰尘，换个SSD，内存配满就好。

怎么换SSD？怎么升级内存？这是两个好问题，但是鉴于机型区别太大，没法写统一的指南，还是Google/Bing吧，
>**别用百度**，求求你们了

装个干净的操作系统，乖乖的用微软的推荐做法。不要在百度上找到野路子装机神器。

一步一步来：按下电脑开机键，进入电脑的UEFI或者BIOS界面，UEFI/BIOS里面，一定有一个选项是“默认设置”，如果看着满屏生词两眼一抹黑，就设置成**默认**。

如果想继续挑战的：可以拿出手机把每个设置项都Google/Bing一番，
>**别用百度**，求求你们了。

**Secure Boot** 要打开，确保你安装的Windows的加载器是经过签名的，

这样一整条信任链可以确保你使用的操作系统是未经修改的，
这条信任链大致是 UEFI->bootloader->OS->Application, 这个知识点要琢磨透了，因为这也会用在很多安全相关的领域，比如数字证书/签名等场合。

TPM如果有的话，要打开，这是获得Windows 11升级推送的必要非充分条件。

如果不用虚拟机的话，
UEFI/BIOS里面虚拟化相关的几个功能可以关闭，
这几个功能有安全上的隐患

>Intel(R) Virtualization Technology

>Intel(R) VT-d Feature

其他功能自己看着办，还是那句话，如果看不懂就留着默认值别瞎改，*unless you know what you are doing*

如果你的电脑是Mac，那么老老实实升级到最新的版本，

别在Mac上面装Win，*unless you know what you are doing*

如果你的电脑是出厂预装了Win10，并且这些年被你装各种软件插的千疮百孔了，那么恢复出厂设置即可。恢复出厂设置的教程可以去微软官网搜。

不要只看野路子教程，可以看看野路子的思路，但是最终还是要遵守官网的教程，*unless you know what you are doing*

如果你的电脑很老，出厂是Win8，Win7，甚至XP，那么搜索“Create installation media for Windows”， 微软官网有教程教你怎么制作一个可启动的U盘来安装。

安装之前记得备份数据，可以用OneDrive或者iCloud备份在云上，也可以用移动硬盘备份在本地。不要用U盘，这东西坏的概率比云和移动硬盘都高。装机行业有句话：硬盘很便宜，硬盘会坏掉。牢记！！

安装的时候，让系统自己去分区，除了系统自己搞出来的保留分区/启动分区之外剩下的一个分区就可以了。

那种分成四个“系统/软件/数据/媒体”区的老黄历做法可以扫进历史的垃圾堆了。

使用的时候老老实实按照微软的库的分法放文件就可以了， *unless you know what you are doing*


系统装好了之后，登录自己的Microsoft ID，OneDrive设置好，然后早点睡觉吧。晚安

# 善待操作系统

有个原则叫做 **最小权限原则**， 大意就是干一件事情如果只需要院子的钥匙，就不要授予大门的钥匙。
这几年大家用智能手机应该深有体会。

传统的桌面操作系统喜欢按照**普通用户**/**管理员**这样按角色划分的方式（role based access control），Win/Mac/Linux的思路基本都是一致的。

智能手机则喜欢按照**位置**/**录音**/**拍照**这些功能划分权限，因为智能手机统统不给管理员权限，除非**root**或者**越狱**。

在生产环境的手机上越狱是给自己打贱籍标签的行为，谁坚持这么做的话，自己开心就好~~

按照对权限的要求，Windows上的软件分这三类

- 安装需要提权，运行也需要提权
- 安装需求提权，运行不需要
- 安装运行都不需要提权

对应的策略是：
- 都不需要提权的软件：放心随意装
- 安装需提权，运行不需要的：准备一台**养蛊**的电脑，专门安装软件，装完了到Program Files或者Program Files (x86)目录里面把软件的目录拷出来用。这台养蛊的电脑定期恢复出厂设置。用互联网职场的骚话就是：高频需求和低频需求的隔离，冷表和热表的分离，总之道理是相通的。
- 都需要提权的软件：能不装就不装，必须要装的话，仔细检查安装包的数字签名，偶尔用的，装在养蛊机上，经常用的，那也只能捏着鼻子装了，比如Office，比如Visual Stuido

我列举一下我用的工具软件，大部分是运行无提权的

先列出只有32位版本的
- AliWangWang //购物
- Audacity //开源音频处理
- BaiduNetdisk  //百度网盘
- cloudmusic //网易云音乐，运行的时候偶尔需要提权，拒绝掉再打开就消停了
- foobar //听歌
- FreeCommander XE //文件管理器
- PDFill //PDF工具
- platform-tools //安卓adb工具，建议安卓手机用户准备
- Privoxy  //科学上网工具
- PsPad  //二进制文件查看器
- TeamViewer //远程管理工具
- Thunder Network  //迅雷
- WeChat //微信

接下来是64位版本的软件
- 7-Zip //开源解压缩
- eclipse //开源集成开发环境
- FileZilla FTP Client //开源FTP客户端，现在更喜欢用命令行的scp和rsync了
- Firefox //开源浏览器
- inkscape //开源矢量图编辑器
- JetBrains //开源集成开发环境
- JPEGView64 //开源64位图片浏览器
- Notepad2.exe
- octave-4.0.3 //开源开发工具，号称Matlab开源替代品
- paint.net //Photoshop免费替代品，华盛顿大学给微软做的DOTNET样板工程
- PortableGit //开源的Windows版本的git
- putty //开源ssh客户端
- Python37 
- SumatraPDF //开源PDF epub mobi阅读器
- syncthing //开源私有云同步工具
- TightVNC
- Transmission //开源BT下载工具
- VcXsrv //开源X Server
- vlc //开源视频播放器，对HEVC支持很好
- VSCode //微软的开源代码编辑器
- Wireshark //抓包工具
- XMind 8 Update 8 //脑图

如果没有多余的电脑可供养蛊怎么办？ 可以用虚拟机，或者用VHD装个系统。这部分明天再说吧。

