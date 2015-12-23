# JavaNESBrain

[Visit the github pages](https://admazzola.github.io/JavaNESBrain)

A fork of the BJNE Java NES Emulator that includes a Neural Network learning algorithm based on SethBling's MarI/O Lua script to enable the emulator to learn how to play itself.  (Eclipse .project file already included, but you can use any IDE)

At this time, the only supported ROM is 'Super Mario Bros' for the NES.  Support for different ROMs can be added by altering the GameDataManager class and telling it which memory addresses to suck the 'tile map' inputs from.

This is still a WIP! It does work -to an extent- but the emulator saving/loading code (added during this project) still causes corruption at times and there are some other issues.  Feel free to submit pull requests.

####Instructions
Run the main function in the 'SuperBrain' class using JRE 7 32 bit.  Once the emulator opens, use the 'open' button in the GUI menu to run the NES ROM of your choice.  Use the keyboard to press 'Enter' and start a level.  Then, use the 'Start AI' button in the GUI menu and your keyboard controls will be relinquished to the evolving neural network.

![preview](https://cloud.githubusercontent.com/assets/6249263/8437567/d564abbe-1f2d-11e5-8457-15d470e0a294.PNG)
