#!/bin/bash

CODE=$1

#step 1: check adb binary
if [[ -n  "which adb 2>/dev/null" ]]
then
    ADBBIN=adb
elif [[ -x "/c/opt/platform/adb.exe" ]]
then
    ADBBIN=/c/opt/platform-tools/adb.exe
elif [[ -x "/d/opt/platform/adb.exe" ]]
then
    ADBBIN=/d/opt/platform-tools/adb.exe
fi

#step 2: check whether screen is on
if [[ "$ADBBIN shell dumpsys power | grep mScreenOn"  =~ "false" ]]
then
    echo "screen is off, turn it on"
    $ADBBIN shell input keyevent 26
fi

#step 3: list all connected devices
DEVS=$($ADBBIN  devices|grep -v devices|awk '{print $1}')

#step 4: iterate between all magic strings
MAGICSTRINGS=($CODE $DAILYSPECIAL "PRRDq1ivvVeZa2Q9" "Jnz5a8KAvvOWCoi2" "q7KPpQfDTWvvIco5" "tlpcc9vBEv19LfWv")
TIMEOUT=10

for m in ${MAGICSTRINGS[@]}
do
    for d in $DEVS
    do
        $ADBBIN -s $d shell input keyevent 4
        $ADBBIN -s $d shell input keyevent 4
        $ADBBIN -s $d shell input keyevent 3
        $ADBBIN -s $d shell am start ca.zgrs.clipper/.Main
        $ADBBIN -s $d shell am broadcast -a clipper.set -e text $m
        $ADBBIN -s $d shell am start com.xunmeng.pinduoduo/.ui.activity.MainFrameActivity
    done
    sleep $TIMEOUT
    sleep $TIMEOUT
    
    for d in $DEVS
    do
        case $d in 
         "T017501VUH" | "ZX1C622R6W" ) #XT1055 1280*720 #XT1079 1280*720
            sleep $TIMEOUT
        	$ADBBIN -s $d shell input tap 360 750
        	$ADBBIN -s $d shell input tap 360 800
        	$ADBBIN -s $d shell input tap 360 850  
            sleep $TIMEOUT
        	;;
         "01adabdf234f675f") #mako 1280*800
            sleep $TIMEOUT
        	$ADBBIN -s $d shell input tap 400 800
            sleep $TIMEOUT
        	;;
         "0A3BBFCF0C012014" | "T0695007W3") #Droid, 960x640
            sleep $TIMEOUT
            sleep $TIMEOUT
            sleep $TIMEOUT
        	$ADBBIN -s $d shell input tap 320 650
            sleep $TIMEOUT
        	;; 
         "Baytrail02234F62") #Ramos, 1920x1200
            sleep $TIMEOUT
            sleep $TIMEOUT
            sleep $TIMEOUT
            sleep $TIMEOUT
            $ADBBIN -s $d shell input tap 600 1160
            $ADBBIN -s $d shell input tap 600 1200
            $ADBBIN -s $d shell input tap 600 1240
            sleep $TIMEOUT
            sleep $TIMEOUT
            sleep $TIMEOUT
        	;; 
         *)
            sleep $TIMEOUT
            $ADBBIN -s $d shell input tap 540 1200
            $ADBBIN -s $d shell input tap 540 1250
            $ADBBIN -s $d shell input tap 540 1300
            $ADBBIN -s $d shell input tap 540 1350
            sleep $TIMEOUT
        	;;
        esac
    done
done