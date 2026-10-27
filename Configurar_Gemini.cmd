@echo off
powershell.exe -NoProfile -STA -ExecutionPolicy RemoteSigned -File "C:\Users\Public\RA_AndroidProject\configurar_gemini.ps1"
if errorlevel 1 pause
