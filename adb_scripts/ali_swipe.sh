#!/bin/bash
export PATH=/d/opt64/scrcpy:$PATH

adb devices|grep UASNW19611006167 || adb connect 192.168.16.252:21218
adb devices|grep 9c31ad62 || adb connect 192.168.16.102:5555 || adb connect 192.168.16.103:5555

android_devices=$(adb devices | awk '/^[^ ]+device$/ {print $1}')
echo found android devices $android_devices

for device in $android_devices;
do
    scrcpy -s $device &
done

for (( i=1; i<=50; i++ )); do
    echo "Starting iteration $i of 50..."
    for device in $android_devices;
    do
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
    sleep 3
    echo "Finished iteration $i."
done
