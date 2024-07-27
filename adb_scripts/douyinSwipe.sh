################################
# 需求：支付宝签到的时候不用动手，
# 方法：adb tcpip 开启手机的无线adb功能，然后用adb的swipe功能
# 任务1：先测试一下看看抖音有没有反作弊功能，比如禁止掉了adb debug
# 任务2：检测连接了多少个手机，取得每个手机的序列号 
# 任务3：循环，每次循环中随机的生成滑动的 起点/终点 和滑动的间隔时间
# 任务4：获取屏幕分辨率，适配不同分辨率的手机，自动生成默认起点/终点坐标
# 任务5：@todo 自动获取电池电量，如果低于20则停止
################################

DEFAULT_TIMEOUT=5

#step 1: check adb binary
if [[ -n  `which adb 2>/dev/null` ]]
then
    echo "default adb found"
    ADBBIN=adb
elif [[ -x "/c/opt/platform/adb.exe" ]]
then
    ADBBIN=/c/opt/platform-tools/adb.exe
elif [[ -x "/d/opt/platform/adb.exe" ]]
then
    ADBBIN=/d/opt/platform-tools/adb.exe
fi

$ADBBIN connect "192.168.16.251:28218"
$ADBBIN connect "192.168.16.102:5555"
$ADBBIN connect "192.168.16.103:5555"

#step 2: list all connected devices
DEVS=$($ADBBIN devices|awk ' match($2, "device") {print $1}')

while :
do
    for d in $DEVS
    do
        SCREEN_WIDTH=$($ADBBIN -s $d shell wm size|awk -F"[ x\r]" '{print $3}')
        SCREEN_HEIGHT=$($ADBBIN -s $d shell wm size|awk -F"[ x\r]" '{print $4}')
        BX=`expr $SCREEN_WIDTH \* 2 / 5  + $((RANDOM %100))`  #bottom x
        UX=`expr $SCREEN_WIDTH \* 2 / 5  + $((RANDOM %100))`  #upper  x
        BY=`expr $SCREEN_HEIGHT \* 4 / 5 + $((RANDOM %150))`  #bottom y
        UY=`expr $SCREEN_HEIGHT / 5 + $((RANDOM %150))`  #upper  y
        TIMEOUT=`expr $DEFAULT_TIMEOUT + $((RANDOM %15))`
        echo ‘BX is $BX, UX is $UX, BY is $BY, UY is $UY,’
        $ADBBIN -s $d shell input swipe $BX $BY $UX $UY 256
        sleep 3
        $ADBBIN -s $d shell input swipe $UX $UY $BX $BY 384
    done
    sleep $TIMEOUT
done