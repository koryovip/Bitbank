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

