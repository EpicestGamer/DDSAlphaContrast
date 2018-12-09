# DDSAlphaContrast
Goes through a set of folders and adjusts the contrast of the alpha channel in Direct Draw Surface files.

This was made when I was using multiple "Consistent Pip-Boy Icons" mods for Fallout 3/NV, I noticed the alpha channels weren't consistent between the two mods and did not want to manually adjust it.

# Used Libraries
https://github.com/Dahie/DDS-Utils

# Usage
This is a command line java program, it runs in the directory it is set in, to run it do `java -jar DDSAlphaContrast.jar` and it will run with the default value (150% contrast)

## Input

### `-v`
This runs the program in verbose mode, it tells you where it is scanning through files and folders and will help you pinpoint which file an error may be occuring with. Also useful if you want to track where it is, or if you're just curious. Not recommended though unless there are actual problems, as it slows down processing a lot more.

### `-c (contrast value)`
Uses a custom contrast value, in the form of a decimal, so 1 is 100% while 0 is 0%, 1.5 is 150%, 2 is 200% etc. Useful if you find that 150% contrast is unsatisfactory.

### `-h`
Prints a help message, if it's the only parameter, that's all the command will do. Basically a briefer version of this.
