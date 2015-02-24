# CivCraft
[![Build Status](https://drone.io/github.com/AlexIIL/CivCraft/status.png)](https://drone.io/github.com/AlexIIL/CivCraft/latest)

A mod for minecraft, based off of Civ V and Civ Beyond Earth and Factorio.

Essentially, a mod that adds a tech tree to minecraft. At the moment (more is planned, check the [issues](https://github.com/AlexIIL/CivCraft/issues?q=is%3Aopen+label%3Aenhancement+author%3AAlexIIL) page for the current plans)

## To compile from scratch
* This assumes you have git, but if you don't, you can download it from the [Git website](http://git-scm.com/)
* Run "git clone https://github.com/AlexIIL/CivCraft.git" in a terminal
* Run "cd CivCraft"
* Run "./gradlew setupCIWorkspace" *(this assumes you are using linux, for Windows use "gradlew.bat" and I don't know what to use on Mac OSX)* 
* Run "./gradlew build"
* The jar's should be in the /build/libs/ folder

## To contribute
* Fork this repository with your own github account
* This assumesyou have git, but if you don't, you can download it from the [Git website](http://git-scm.com/)
* Run "git clone https://github.com/YOUR_NAME/CivCraft.git" in a terminal
* Run "cd CivCraft"
* Run "./gradlew setupDecompWorkpspace"
* Run "./gradlew eclipse"
* Run, when you have finished your changes "git push"
* Submit a pull request with the changes.
Please either leave info on what you did, unless it is a simple bug-fix.

Latest build at [drone.io](https://drone.io/github.com/AlexIIL/CivCraft/files).
