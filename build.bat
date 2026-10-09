@echo off
REM ============================================================
REM  MITS - Intelligent Medical Inventory Tracking System
REM  Build Script for Windows
REM  Compiles all Java sources into the ./out directory
REM ============================================================

echo ============================================
echo  Building MITS Project...
echo ============================================

REM Clean old build
if exist out (
    rmdir /s /q out
)
mkdir out

REM Find all .java source files
echo Compiling all .java files from src\ ...

dir /s /b src\*.java > sources.txt
javac -d out -sourcepath src @sources.txt

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Check errors above.
    del sources.txt
    exit /b 1
)

del sources.txt

echo.
echo [SUCCESS] Build completed successfully!
echo Output directory: .\out
echo.
echo Run with:
echo   run_gui.bat       - Launch the Swing GUI application
echo   run_console.bat   - Launch the console application
echo   run_server.bat    - Launch the TCP inventory server
echo   java -cp out com.mits.app.TestRunner  - Run all tests
echo ============================================
