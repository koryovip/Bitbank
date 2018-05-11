@echo off
cd /d %~dp0

call setClassPath.bat

start javaw -Xms64m -Xmx64m -cp %CLASSPATH% pubnub.TransationForm

rem echo END
rem pause > nul