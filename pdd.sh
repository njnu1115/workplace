#!/bin/bash

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

#step 2: list all connected devices
DEVS=$($ADBBIN devices|awk ' match($2, "device") {print $1}')

#step 3: check whether screen is on
for d in $DEVS
do
#    if [[ `$ADBBIN -s $d shell dumpsys power | grep mScreenOn 2>/dev/null`  =~ "false" ]]
#    then
#        echo "screen of $d is off, turn it on"
        $ADBBIN -s $d shell input keyevent 224
#    else
#        echo "screen of $d is on"
#    fi
done

TIMEOUT=55

#step 4: iterate all input strings
if [ "$1" -gt 0 ] 2>/dev/null ;then 
    echo "========== $1 is pure number============" 
    for m in $*
    do

        for d in $DEVS
        do
            $ADBBIN -s $d shell am force-stop com.xunmeng.pinduoduo
            $ADBBIN -s $d shell input keyevent 4
            sleep 1
            $ADBBIN -s $d shell input keyevent 3
            sleep 1
            $ADBBIN -s $d shell am start ca.zgrs.clipper/.Main
            sleep 1
            $ADBBIN -s $d shell am broadcast -a clipper.set -e text $m
            sleep 1
            $ADBBIN -s $d shell am start com.xunmeng.pinduoduo/.ui.activity.MainFrameActivity
        done
        sleep $TIMEOUT
    done
else
    echo '=========This is ZhuShui==============='
    for d in $DEVS
    do
        $ADBBIN -s $d shell am force-stop com.xunmeng.pinduoduo
        $ADBBIN -s $d shell am start ca.zgrs.clipper/.Main
        sleep 1
        $ADBBIN -s $d shell am broadcast -a clipper.set -e text $1
        sleep 1
        $ADBBIN -s $d shell am start com.xunmeng.pinduoduo/.ui.activity.MainFrameActivity
    done
    sleep $TIMEOUT
fi
