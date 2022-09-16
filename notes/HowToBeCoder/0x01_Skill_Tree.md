如果时光倒流，遇到还在读书的我自己，我会怎样在几张A4纸的篇幅给我自己一些指南，让当年的我少走一些弯路？

限于篇幅肯定不能写成手把手的指南，只能列一些方向，原则

# 关于知识的知识

>**知道比知识重要**

>**学会区分事实和观点**

>**提问的智慧**

原文网址：http://www.catb.org/~esr/faqs/smart-questions.html
中文翻译：https://lug.ustc.edu.cn/wiki/doc/smart-questions/

>**别用百度**

# 准备硬件

无论手头有什么电脑，清清灰尘，换个SSD，内存配满就好。

电脑和手机是吃饭的家伙，花点时间和精力好好维护之的性价比很高，值得在上面投入时间和精力。但不要折腾它们。从认真读手册开始。


>**只用来路可信的软件**

微软官网下载的Windows和Office镜像，输入法/防火墙/杀毒这些只要操作系统有自带的，就不碰第三方的

>**不懂的地方，要么遵守最佳实践指南，要么仔细读手册**


# 善待软件

使用过程中，遵循
>**最小权限原则**

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
- FileZilla FTP Client //开源FTP客户端，现在更喜欢用命令行的scp和rsync了
- Firefox //开源浏览器
- inkscape //开源矢量图编辑器
- JetBrains //开源集成开发环境
- JPEGView64 //开源64位图片浏览器
- Notepad2.exe // 记事本的替代品
- octave //开源开发工具，号称Matlab开源替代品
- paint.net //Photoshop免费替代品，华盛顿大学给微软做的DOTNET样板工程
- PortableGit //开源的Windows版本的git
- putty //开源ssh客户端
- SumatraPDF //开源PDF epub mobi阅读器
- syncthing //开源私有云同步工具 
- TightVNC //VNC远程控制工具
- Transmission //开源BT下载工具
- VcXsrv //开源X Server
- vlc //开源视频播放器，对HEVC支持很好
- VSCode //微软的开源代码编辑器
- Wireshark //抓包工具
- XMind 8 Update 8 //脑图

#  工具的工具

## 如何管理知识

包括但不限于 
- 如何记笔记
- 如何归档资料
- 如何备份
- 如何同步

### 如何记录
尽量用标记语言记笔记，而不是用所见即所得的工具，理由是方便版本管理，方便检索，例如 Markdown。

想吃透Markdown， 可以继续深究或扩展下面这几个知识点

- Mark language(including XML, HTML, etc.) 
- dot & graphviz 
- PDF format 
- LaTex

### 如何保管
公有云
- github   版本管理plain text格式的内容
- OneDrive 存Office格式的文档
- iCloud   存照片

私有云

自己搭私有云存放那些不方便放在公有云的内容，比如涉密的
- rsync 吃透rsync最好
- syncthings 用syncthings这样的工具也行，方便

### 如何使用
TBD


# 工具

- 虚拟机/沙盒/容器 //隔离是优秀的运维实践方法
TBD

# 元知识

有些知识如果不了解可能会无法理解世界是如何运转的
- Hash
- 非对称加密
- 遗传进化算法
