@echo off

SET CURRENTDIR="%~dp0"
set BASHHOME=C:\Users\gaston\AppData\Local\Atlassian\SourceTree\git_local

cd %CURRENTDIR%\..
start %BASHHOME%\git-bash.exe