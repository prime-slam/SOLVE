# SLAM Oriented Landmark Visualizer and Editor


[![Build Status](https://github.com/prime-slam/SOLVE/workflows/Build/badge.svg)](https://github.com/prime-slam/SOLVE/actions/workflows/build.yml)

The aim of the project is to create a powerful desktop application to make debugging of SLAM frontend algorithms easier.

This tool will provide following features:

* Visualization of landmarks of three types (points, lines, planes)
* Mapping of the same landmarks between different images based on SLAM data association
* Editing and fine-tuning landmarks positions and association

## How do I create a project of the correct format?
1. Create the Images folder, in which we put all the frames from the dataset. They should be numbered in order and be in jpg or png format.
2.  Create folders based on the number of algorithms present in the project. You need to name them in the format name-algorithm_landmark (example: alg1_line)
3. Put files with markup in each of the folders. They should be numbered according to the frames in the project
    1. Points are represented in csv file format, where the first line is used as a header (uid,x,y), and subsequent lines contain data directly (uid, x and y coordinates separated by commas)
    2. Lines are represented in csv file format, where the first line is used as a header (uid,x0,y0,x1,y1), and subsequent lines contain data directly (uid, coordinates of the beginning and coordinates of the end of the line, separated by commas)
    3. The planes are represented as a png file.
## Build

Ensure you have JDK 17 to build or run this project.

Build: `./gradlew assemble`  
Run: `./gradlew run`  