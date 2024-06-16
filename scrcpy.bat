@echo off  
@REM setlocal enabledelayedexpansion  

SET ADBCMD=D:\opt64\scrcpy\adb.exe
SET SCRCPYCMD=D:\opt64\scrcpy\scrcpy.exe

for %%D in ("192.168.16.251:28218","192.168.16.103:5555") do (
    @REM echo %%D
    %ADBCMD% connect %%D
    start %SCRCPYCMD% -s %%D
)

pause