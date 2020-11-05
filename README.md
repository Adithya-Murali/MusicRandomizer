# MusicRandomizer
A small code for a silly task.

This code inserts a random number infront of the title of the song, so that devices that do not have shuffle play will get a random order.

There are 2 main classes:
- RenameInPlace:This renames the songs without affecting the file structure.
- RenameToOneFolder: This will open each directory and enter the renamed songs into the same main directory and delete the empty directories. This is so that we can have complete randomization.
There is an additional option to add an exclusion list (a text file containing names of folders to be ignored).

## Commands to be performed.
- To make the jar, 
    `mvn clean package` 
- Move the jar to the folder which contains all the songs. 
- On terminal, point to that folder and run either of the below:
```
    java -cp RandomizeMusic-1.0-SNAPSHOT.jar RenameInPlace      // To rename without affecting folder heirarchy
    java -cp RandomizeMusic-1.0-SNAPSHOT.jar RenameToOneFolder  // To combine contents of all folders into the main folder and rename
    java -cp RandomizeMusic-1.0-SNAPSHOT.jar RenameToOneFolder C:/js/fd/excludes.txt // Pass path of text file which contains names of directories (relative to the directory which contains the jar) to be ignored.
```

## Context
My grandfather is obsessed with carnatic music, so much so that he spends his every waking hour with his speaker. When I visited him over the weekend, he told me that it always plays the songs in the same order.
Turns out there is no shuffle function in the device, and it plays in alphabetic order. 
So I decided to write a small utility that will insert a random number infront of the title, so that when I visit them, I can just plug in the USB and run the shuffle application.
This is by no means the best solution, but it was quick and it helped and that was enough.
