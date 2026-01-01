#!/bin/bash

packages=$(adb shell pm list packages -3 | sed 's/package://g')

for pkg in $packages; do
    if [ "$pkg" != "com.termux" ]; then
        echo "Killing package: $pkg"
        adb shell am force-stop "$pkg"
    fi
done

echo "All non-Termux 3rd-party packages have been stopped."
