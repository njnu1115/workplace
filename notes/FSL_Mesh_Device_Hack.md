# 佛山照明智能插座研究笔记

19.9买了个佛山照片的智能插座，买之前就知道这东西只能和**天猫精灵**搭配使用。心想，先买回来试试看，能不能绕过天猫精灵直接用手机控制。

到手了之后，发现这是一个蓝牙BLE MESH设备。开始查[手册](https://developer.aliyun.com/article/751771?spm=a2c6h.12873581.0.dArticle751771.ca33589ddXMXyr&groupCode=genieaiot)

应该是里面提到的2.2 单孔插座这一类型。

连接过程的[规范定义] (https://www.aligenie.com/doc/357554/gtgprq)

> 提到：mesh设备在Provisioning Capabilities阶段提供OOB方式，要求唯一支持Static OOB方式

这就比较烦了，**只支持**static OOB，意味着密钥是写死在固件里面的，只有固件开发者和云端两头知道。 

但是，事实上，经过测试发现，并非**只支持**static OOB，还支持no OOB。这就很方便了。

说到这个OOB，Out-Of-Band，带外配对，名字起的相当唬人。其实意思就是：用蓝牙以外的技术交换密钥。

什么叫做**蓝牙以外的技术**呢？  比如NFC，比如WIFI，比如硬编码写死的，总之就是别用蓝牙就行。

在[蓝牙官网](https://www.bluetooth.com/blog/proving-mesh-interoperability/) 找到一个推荐的手机端Mesh App

[nRF Mesh](https://itunes.apple.com/us/app/nrf-mesh/id1380726771?mt=8) for iOS devices;

[nRF Mesh](https://play.google.com/store/apps/details?id=no.nordicsemi.android.nrfmeshprovisioner) for Android devices;

安装之后一番骚操作，可以实现On/Off开关了。 定时按理说也是可以设置的，但是不知道具体应该往哪个Model里面填什么值。等找到了再回来补充。

截获的部分信息：
```bash
Provisioning Information

Element Count: 1

Algorithm Type: 1

Public Key Type: [B@9b5860d

Static OOB Type: 1

Output OOB Size: 0

Input OOB Size: 0

Input OOB Action:  Push

Application Key 1: 9518968EF8D41F16D37F13BC9C41A414

Application Key 2: 0776702BF5E6D59D262A3EF03D217078

Application Key 3: 4996F178B485FC930F465C0DE7F9867B
```

```bash
以下是HCI工具截获的Adv包
--------------------------------------------------------------------
[1093] : <Rx> - 01:09:40.545
-Type           : 0x04 (Event)
-EventCode      : 0x00FF (HCI_LE_ExtEvent)
-Data Length    : 0x3C (60) bytes(s)
 Event          : 0x0613 (1555) (GAP_AdvertiserScannerEvent)
 Status         : 0x00 (0) (SUCCESS)
 EventId        : 0x00400000 (4194304) (
                  GAP_EVT_ADV_REPORT)
 AdvRptEventType: 0x13 (19) (Legacy_ADV_IND_or_Data_Complete)
 AddressType    : 0x00 (0) (ADDRTYPE_PUBLIC)
 Address        : F8:A7:63:7C:98:82
 PrimaryPHY     : 0x01 (1) (SCANNED_PHY_1M)
 SecondaryPHY   : 0x00 (0) (SCANNED_PHY_NONE)
 AdvSid         : 0xFF (255)
 TxPower        : 0x7F (127)
 RSSI           : 0xC1 (193)
 DirectAddrType : 0xFF (255) (ADDRTYPE_NONE)
 DirectAddr     : 00:00:00:00:00:00
 PeriodicAdvInt : 0x0000 (0)
 DataLength     : 0x001D (29)
 Data           : 02:01:06:03:02:27:18:15:16:27:18:A8:01:71:77:0E:
                  00:00:82:98:7C:63:A7:F8:02:00:00:00:00
Dump(Rx):
0000:04 FF 3C 13 06 00 00 00 40 00 13 00 82 98 7C 63 ..<.....@.....|c
0010:A7 F8 01 00 FF 7F C1 FF 00 00 00 00 00 00 00 00 ................
0020:1D 00 02 01 06 03 02 27 18 15 16 27 18 A8 01 71 .......'...'...q
0030:77 0E 00 00 82 98 7C 63 A7 F8 02 00 00 00 00    w.....|c.......
--------------------------------------------------------------------
02:01:06:03:02:27:18:15:16:27:18: 
A8:01: 公司ID，设置为0x01A8：Taobao
71:  ‭0b01110001‬， Bit0-3 蓝牙广播包版本号，目前是0x01，bit4为1：一机一密。 bit5为1：支持OTA。bit6~7：01：BLE4.2
77:0E:00:00: 阿里巴巴平台颁发，一型一号
82:98:7C:63:A7:F8:阿里巴巴平台颁发，一机一号
02: 0b00000010: bit7-1：uuid版本号，目前版本为1; bit0  0：处于未配网广播状态
00:00:00:00 Reserved for future use



--------------------------------------------------------------------
[1176] : <Rx> - 01:09:41.451
-Type           : 0x04 (Event)
-EventCode      : 0x00FF (HCI_LE_ExtEvent)
-Data Length    : 0x34 (52) bytes(s)
 Event          : 0x0613 (1555) (GAP_AdvertiserScannerEvent)
 Status         : 0x00 (0) (SUCCESS)
 EventId        : 0x00400000 (4194304) (
                  GAP_EVT_ADV_REPORT)
 AdvRptEventType: 0x10 (16) (Legacy_ADV_NONCONN_or_Data_Complete)
 AddressType    : 0x00 (0) (ADDRTYPE_PUBLIC)
 Address        : F8:A7:63:7C:98:82
 PrimaryPHY     : 0x01 (1) (SCANNED_PHY_1M)
 SecondaryPHY   : 0x00 (0) (SCANNED_PHY_NONE)
 AdvSid         : 0xFF (255)
 TxPower        : 0x7F (127)
 RSSI           : 0xC0 (192)
 DirectAddrType : 0xFF (255) (ADDRTYPE_NONE)
 DirectAddr     : 00:00:00:00:00:00
 PeriodicAdvInt : 0x0000 (0)
 DataLength     : 0x0015 (21)
 Data           : 14:2B:00:A8:01:71:77:0E:00:00:82:98:7C:63:A7:F8:
                  02:00:00:00:00
Dump(Rx):
0000:04 FF 34 13 06 00 00 00 40 00 10 00 82 98 7C 63 ..4.....@.....|c
0010:A7 F8 01 00 FF 7F C0 FF 00 00 00 00 00 00 00 00 ................
0020:15 00 14 2B 00 A8 01 71 77 0E 00 00 82 98 7C 63 ...+...qw.....|c
0030:A7 F8 02 00 00 00 00                            .......
--------------------------------------------------------------------
```