@echo off
cd /d "%~dp0"
javac -d out src\model\*.java src\usermanagement\*.java src\businesslogic\*.java src\reporting\*.java src\utils\*.java src\dataprocessing\*.java src\network\*.java src\gui\*.java
if errorlevel 1 (
    echo Compilation failed. Check that the JDK is installed.
    pause
    exit /b 1
)
java -cp out gui.Main %*
pause
