@echo off
cd /d %~dp0

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;..\BBItfc\bin
set CLASSPATH=%CLASSPATH%;.\bin
set CLASSPATH=%CLASSPATH%;.\lib\*
set CLASSPATH=%CLASSPATH%;.\lib\jfinal\jfinal-3.3-bin.jar
set CLASSPATH=%CLASSPATH%;.\lib\jfinal\jfinal-3.3-lib\druid\druid-1.0.29.jar
set CLASSPATH=%CLASSPATH%;.\lib\math\*
set CLASSPATH=%CLASSPATH%;.\lib\twitter\*
echo %CLASSPATH%

java -Xms128m -Xmx128m -cp %CLASSPATH% auto3.Start15M

rem echo END
pause > nul