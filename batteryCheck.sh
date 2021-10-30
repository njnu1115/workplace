#!/bin/bash

CODE=$1

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
DEVS=$($ADBBIN  devices|grep -v devices|awk '{print $1}')

#step 4: 
for d in $DEVS
do
    echo $d
    $ADBBIN -s $d shell dumpsys battery | grep level
done
