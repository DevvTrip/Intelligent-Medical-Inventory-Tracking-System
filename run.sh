#!/bin/bash
cd "$(dirname "$0")"
javac -d out src/model/*.java src/usermanagement/*.java src/businesslogic/*.java \
    src/reporting/*.java src/utils/*.java src/dataprocessing/*.java \
    src/network/*.java src/gui/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed. Check that the JDK is installed."
    exit 1
fi
java -cp out gui.Main "$@"
