#!/bin/bash
# Launch the MITS Swing GUI Application
echo "Starting MITS GUI Application..."
java -cp out com.mits.app.GuiApp "$@"
