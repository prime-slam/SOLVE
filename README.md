# SLAM Oriented Landmark Visualizer and Editor


[![Build Status](https://github.com/prime-slam/SOLVE/workflows/Build/badge.svg)](https://github.com/prime-slam/SOLVE/actions/workflows/build.yml)

The aim of the project is to create a powerful desktop application to make debugging of SLAM frontend algorithms easier.

This tool will provide following features:

* Visualization of landmarks of three types (points, lines, planes)
* Mapping of the same landmarks between different images based on SLAM data association
* Editing and fine-tuning landmarks positions and association

## Build

Ensure you have JDK 17 to build or run this project.

Build: `./gradlew assemble`  
Run: `./gradlew run`  