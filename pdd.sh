#!/bin/bash

CODE=$1
DAILYSPECIAL="3Q1W7IvzeL8fs5tm "
ADBBIN=adb
DEVS=$($ADBBIN  devices|grep -v devices|awk '{print $1}')
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
            $ADBBIN -s $d shell input tap 600 1200
            sleep $TIMEOUT
            sleep $TIMEOUT
            sleep $TIMEOUT
        	;; 
         *)
            sleep $TIMEOUT
            $ADBBIN -s $d shell input tap 540 1200
            sleep $TIMEOUT
        	;;
        esac
    done



done