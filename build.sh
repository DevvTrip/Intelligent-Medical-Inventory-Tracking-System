#!/bin/bash
# ============================================================
#  MITS - Intelligent Medical Inventory Tracking System
#  Build Script for Linux / macOS
#  Compiles all Java sources into the ./out directory
# ============================================================

echo "============================================"
echo " Building MITS Project..."
echo "============================================"

# Clean old build
rm -rf out
mkdir -p out

# Find all .java source files and compile
echo "Compiling all .java files from src/ ..."

find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt
RESULT=$?

rm -f sources.txt

if [ $RESULT -ne 0 ]; then
    echo ""
    echo "[ERROR] Compilation failed! Check errors above."
    exit 1
fi

echo ""
echo "[SUCCESS] Build completed successfully!"
echo "Output directory: ./out"
echo ""
echo "Run with:"
echo "  ./run_gui.sh       - Launch the Swing GUI application"
echo "  ./run_console.sh   - Launch the console application"
echo "  ./run_server.sh    - Launch the TCP inventory server"
echo "  java -cp out com.mits.app.TestRunner  - Run all tests"
echo "============================================"
