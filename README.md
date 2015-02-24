# CivCraft
[![Build Status](https://drone.io/github.com/AlexIIL/CivCraft/status.png)](https://drone.io/github.com/AlexIIL/CivCraft/latest)

A mod for minecraft, based off of Civ V and Civ Beyond Earth and Factorio.

Essentially, a mod that adds a tech tree to minecraft. At the moment (more is planned, check the [issues](https://github.com/AlexIIL/CivCraft/issues) page for the current plans)

## To compile from scratch
* Run "git clone https://github.com/AlexIIL/CivCraft.git" in a terminal
* then "cd CivCraft"
* then "./gradlew setupCIWorkspace" *(this assumes you are using linux, for Windows use "gradlew.bat" and I don't know what to use on Mac OSX)* 
* then "./gradlew build"
* and the jar's should be in the /build/libs/ folder

## To contribute
* Fork this repository with your own github account
* Run "git clone https://github.com/<YOUR_NAME>/CivCraft.git" in a terminal
* then "cd CivCraft"
* then "./gradlew setupDecompWorkpspace"
* then "./gradlew eclipse"
* then, when you have finished your changes "git push"
* and submit a pull request with the changes.
Please either leave info on what you did, unless it is a simple bug-fix.

Latest build at [drone.io](https://drone.io/github.com/AlexIIL/CivCraft/files).
