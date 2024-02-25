# SLAM Oriented Landmark Visualizer and Editor

[![Build Status](https://github.com/prime-slam/SOLVE/workflows/Build/badge.svg)](https://github.com/prime-slam/SOLVE/actions/workflows/build.yml)

The aim of the project is to create a powerful desktop application to make debugging of SLAM frontend algorithms easier.

This tool will provide following features:

* Visualization of landmarks of three types (points, lines, planes)
* Mapping of the same landmarks between different images based on SLAM data association
* Editing and fine-tuning landmarks positions and association

## Important!

At the moment, the visualization of landmarks and frames is being converted to OpenGL rendering, and most of the functionality of the application **in the master branch** of the repository **does not work**!

If you want to take advantage of **all the features of the application**, use this repository branch: [https://github.com/prime-slam/SOLVE/tree/old-scene-realization](https://github.com/prime-slam/SOLVE/tree/old-scene-realization).

## Get started

Follow instructions in the [User Guide](https://github.com/prime-slam/SOLVE/wiki/How-to-get-started)

## How do I create a project of the correct format?

1. Create the ```Images``` folder, in which put all the frames from the dataset. Images must match the following
   criteria:
    * same format for all images
    * jpg or png extension
    * file name is timestamp in Long format
2. For each used landmark detection algorithm create a separate folder. You need to name it in the
   format ```name_algorithm_landmark``` (example: ```alg1_line```, ```alg1_keypoint```, ```alg1_plane```)
3. Put files with markup in each of the folders. They should be named according to the frame they belong to (timestamp)
    1. Points are represented in .csv file with (uid,x,y) columns, where uid is id of landmark and x,y are coordinates of
       keypoint. <code>[Example](https://github.com/prime-slam/SOLVE/tree/master/testData/TestProject2/alg1_keypoint)</code>
    2. Lines are represented in .csv file with (uid,x0,y0,x1,y1) columns, where uid is id of landmark and x0,y0,x1,y1 are coordinates of the
       beginning and end of the
       line. <code>[Example](https://github.com/prime-slam/SOLVE/tree/master/testData/LinesAndKeyPointsProject/alg1_line)</code>
    3. The planes are represented as a png file, where
       the points belonging to the planes are shown in different
       pixel colors. For points that do not belong
       to any plane, black is
       used. <code>[Example](https://github.com/prime-slam/SOLVE/tree/master/testData/PlanesProject/alg1_plane)</code>

## Build

Ensure you have JDK 17 to build or run this project.

Build: `./gradlew assemble`  
Run: `./gradlew run`
