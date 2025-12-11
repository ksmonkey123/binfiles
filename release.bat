@echo off
setlocal

git fetch --tags
if errorlevel 1 exit /b 1

call mvn releaser:release
if errorlevel 1 exit /b 1

git push --tags
if errorlevel 1 exit /b 1