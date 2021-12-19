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
fi

#step 2: list all connected devices
DEVS=$($ADBBIN devices|awk ' match($2, "device") {print $1}')

#step 3: check whether screen is on
for d in $DEVS
do
    if [[ `$ADBBIN -s $d shell dumpsys power | grep mScreenOn 2>/dev/null`  =~ "false" ]]
    then
        echo "screen of $d is off, turn it on"
        $ADBBIN -s $d shell input keyevent 26
    else
        echo "screen of $d is on"
    fi
done


#step 4: iterate between all magic strings
MAGICSTRINGS=($CODE "PRRDq1ivvVeZa2Q9" "Jnz5a8KAvvOWCoi2" "q7KPpQfDTWvvIco5" "tlpcc9vBEv19LfWv")
TIMEOUT=10

#for m in ${MAGICSTRINGS[@]}
for m in $*
do
    for d in $DEVS
    do
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
    sleep $TIMEOUT
    sleep $TIMEOUT
    sleep $TIMEOUT
    sleep $TIMEOUT
    sleep $TIMEOUT
    sleep $TIMEOUT
    sleep $TIMEOUT
done