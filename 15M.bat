@echo off
cd /d %~dp0

call setClassPath.bat

java -Xms128m -Xmx128m -cp %CLASSPATH% auto3.Start15M

rem echo END
pause > nul