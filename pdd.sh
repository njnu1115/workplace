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

# HW-G610
$ADBBIN connect 192.168.16.200:33333

#mi 6x
$ADBBIN connect 192.168.16.221:33333

# XT897
$ADBBIN connect 192.168.16.202:33333
# XT897
$ADBBIN connect 192.168.16.203:33333

# Baytrail
$ADBBIN connect 192.168.16.204:33333

# ZUK-Z2
#$ADBBIN connect 192.168.16.249:33333
# SM-T350
# $ADBBIN connect 192.168.16.252:33333
# SM-T350
# $ADBBIN connect 192.168.16.253:33333
# SM-C7010
$ADBBIN connect 192.168.16.254:33333

#Ghost
$ADBBIN connect 192.168.16.42:33333

$ADBBIN devices 

#step 2: list all connected devices
DEVS=$($ADBBIN devices|awk ' match($2, "device") {print $1}')

#step 3: check whether screen is on
funcCheckScreenOn(){
    if [[ `$ADBBIN -s $1 shell dumpsys power | grep mScreenOn 2>/dev/null`  =~ "false" ]]
    then
        echo "screen of $1 is off, turn it on"
        $ADBBIN -s $1 shell input keyevent 26
    elif [[ `$ADBBIN -s $1 shell dumpsys power | grep mScreenOn 2>/dev/null`  =~ "true" ]]
    then
        echo "screen of $1 is already on"
    elif [ $? -ne 0 ]
    then
        echo "screen of $1 is unknown, use keyevent 224 to turn it on"
        $ADBBIN -s $1 shell input keyevent 224
    else
        echo "=====unknown situation, please contact administrator!!!====="
    fi  
}

TIMEOUT=180

#step 4: iterate all input strings
for m in $*
do
    for d in $DEVS
    do
        funcCheckScreenOn $d
        echo "==== handling $m =========="
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


#$ADBBIN disconnect 192.168.16.250:33333
#$ADBBIN disconnect 192.168.16.252:33333
#$ADBBIN disconnect 192.168.16.253:33333
#$ADBBIN disconnect 192.168.16.254:33333
