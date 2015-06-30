# JavaNESBrain

A fork of the HalfNES Java NES Emulator that includes a Neural Network learning algorithm based on SethBling's MarI/O Lua script to enable the emulator to learn how to play itself.

Dependencies for Windows x64 are included in the 'lib' and 'natives' folders

##Building
Clone this repo in Eclipse as a new project and run the main function in the 'SuperBrain' class


####Instructions
Once the emulator opens, use the 'open' button in the GUI menu to run the NES ROM of your choice.  Use the keyboard to start a level.  Then, use the 'Start AI' button in the GUI menu and your keyboard controls will be relinquished to the evolving neural network.
