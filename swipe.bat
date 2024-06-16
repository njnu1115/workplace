@echo off  
@REM setlocal enabledelayedexpansion  

SET ADBCMD=D:\opt64\scrcpy\adb.exe

@REM set count=5
  
@REM for %%dev in ("192.168.16.251","192.168.16.103") do (  

@REM )
  
  
@REM echo Swipe Done
@REM pause

for /l %%i in (1,1,64) do (  
    for %%D in ("192.168.16.251:28218","192.168.16.103:5555") do (
        @REM echo %%D
        %ADBCMD% connect %%D
        echo executing swipe UP for  ...
        %ADBCMD% -s %%D shell input swipe 540 1900 540 640 200
        timeout /T 3 /NOBREAK
        echo executing swipe DOWN for  ...
        %ADBCMD% -s %%D shell input swipe 540 640 540 1900 200
    )
)
pause