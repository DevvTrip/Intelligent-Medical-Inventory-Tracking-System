@echo off
REM Launch the MITS TCP Inventory Server
echo Starting MITS Inventory Server...
java -cp out com.mits.app.ServerApp %*
