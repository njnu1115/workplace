#!/bin/bash

# 检查目录/d/opt是否存在
if [ -d "/d/opt64/scrcpy" ]; then
    export PATH=$PATH:/d/opt64/scrcpy
elif [ -d "/Users/cl/opt/platform-tools" ]; then
    export PATH=$PATH:/Users/cl/opt/platform-tools
fi

echo "Current PATH: $PATH"

# adb devices|grep UASNW19611006167 || adb connect 192.168.16.251:28218 || adb connect localhost:33333
# adb devices|grep 9c31ad62 || adb connect 192.168.16.102:5555 || adb connect 192.168.16.103:5555
adb devices|grep UASNW19611006167 || adb connect localhost:33333

adb devices|grep -w UASNW19611006167
if [ $? -eq 0 ]; then
    android_devices=UASNW19611006167
fi

adb devices|grep -w '192.168.16.251:28218'
if [ $? -eq 0 ]; then
    android_devices=192.168.16.251:28218
fi

adb devices|grep -w 'localhost:33333'
if [ $? -eq 0 ]; then
    android_devices='localhost:33333'
fi

# android_devices=$(adb devices | awk '/^[^ ]+device$/ {print $1}')
echo found android devices $android_devices

# for device in $android_devices;
# do
#     scrcpy -s $device --encoder='OMX.google.h264.encoder' &
# done

# alipays://platformapi/startapp?appId=20000160

for (( i=1; i<=100; i++ )); do
    echo "Starting iteration $i of 100..."
    for device in $android_devices;
    do
        if [ $((i % 7)) -eq 0 ]; then
            adb -s $device shell input keyevent 4
            sleep 1
            adb -s $device shell input swipe 540 500 540 1600 89
            sleep 1
            adb -s $device shell input tap 900 1200
            # adb -s $device shell input tap 900 1350
            sleep 2
        fi
        SCREEN_WIDTH=$(adb -s $device shell wm size|awk -F"[ x\r]" '{print $3}')
        SCREEN_HEIGHT=$(adb -s $device shell wm size|awk -F"[ x\r]" '{print $4}')
        BX=`expr $SCREEN_WIDTH \* 2 / 5  + $((RANDOM %100))`  #bottom x
        UX=`expr $SCREEN_WIDTH \* 2 / 5  + $((RANDOM %100))`  #upper  x
        BY=`expr $SCREEN_HEIGHT \* 4 / 5 + $((RANDOM %150))`  #bottom y
        UY=`expr $SCREEN_HEIGHT / 5 + $((RANDOM %150))`  #upper  y
        adb -s $device shell input swipe $BX $BY $UX $UY 256
        TEMPERATURE=$(adb -s $device shell dumpsys battery | awk '/^  temperature/ {print $2}')
        if [ "$TEMPERATURE" -gt 420 ]; 
        then
            echo "TOOOOO HOT!!!!" $TEMPERATURE
        fi
    done
    sleep 2
    echo "Finished iteration $i."
done
