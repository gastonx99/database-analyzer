@echo off

SET CURRENTDIR="%~dp0"
set BASHHOME=C:\Progra~1\Git
set MAVEN_HOME=D:\java\apache-maven-3.3.9
set ANT_HOME=D:\java\apache-ant-1.9.6
set JAVA_HOME=C:\Progra~1\Java\jdk1.8.0_73

set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%ANT_HOME%\bin;%PATH%

cd %CURRENTDIR%\..
start %BASHHOME%\git-bash.exe