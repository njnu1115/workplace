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
elif [[ -x "/cygdrive/c/opt/platform-tools/adb.exe" ]]
then
    ADBBIN=/cygdrive/c/opt/platform-tools/adb.exe
elif [[ -x "/cygdrive/d/opt/platform-tools/adb.exe" ]]
then
    ADBBIN=/cygdrive/d/opt/platform-tools/adb.exe
fi

#step 2: list all connected devices
DEVS=$($ADBBIN devices|awk ' match($2, "device") {print $1}')

#step 4: 
for d in $DEVS
do
    model=$($ADBBIN -s $d shell getprop ro.product.model|tr -d '\n\r\ ')
    level=$($ADBBIN -s $d shell dumpsys battery | grep 'level'|head -1|tr -d '\n\r')
    temp=$($ADBBIN -s $d shell dumpsys battery | grep 'temperature'|tr -d '\n\r')
    printf "% 16s\t" $d
    printf "% 16s\t" $model
    printf "%s|" $level
    printf "%s" $temp
    printf "\n"

    $ADBBIN -s $d shell am force-stop com.tencent.mm
    $ADBBIN -s $d shell am force-stop com.xunmeng.pinduoduo
done
