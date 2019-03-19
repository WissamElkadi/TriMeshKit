# TriMeshKit
TriMeshKit is a cross-platform triangular mesh processing SDK that uses half edge data structure "OpenMesh" to represent triangular mesh and "eigen as a math library.


## Building SDK
### Get the Prerequisites
* CMake (tested with 3.7.2)
* Visual Studio (tested with 2015 in 64 bit mode)
* Git
* Ninja
* Android NDK (tested with NDK r19c)
* Android SDK (tested with SDK )
* Java (JDK 1.8)
* Gradle (tested with gradle 5.2.1)
* Android Studio (tested with 3.3.2)

Make sure CMake, Git, MSBuild, Android NDK, Android SDK, JDK, gradle  and Ninja are in your path.

### Environment Variable
* ANDROID_HOME
* ANDROID_NDK
* GRADLE_HOME
* JAVA_HOME

### Clone this repository:
    git bash> git clone git@github.com:WissamElkadi/TriMeshKit.git

### Building the SDK
Execute the following command to build the SDK

git bash> cd build

use -t to select one of the supported platforms {android, windows }, is a mandatory argument.

#### Android Build

    git bash> ./build.sh -t android

default is building both debug and release for both supported architecture "armeabi-v7a" and "arm64-v8a"

use -a to select specific android architecture either { all, armeabi-v7a, arm64-v8a}, default {all}

use -b to select one of build type or both {all, debug, release}, default {all}

use -o  to choose the output directory name, default {trimeshkit_build}

Example:
build Android arm64-v8a release

    git bash> ./build.sh -t android -a arm64-v8a -b release

#### Windows Build

    git bash> ./build.sh -t windows

default is building both debug and release for "Win64"

use -a to select specific android architecture either { all, Win64}, default {all}

use -b to select one of build type or both {all, debug, release}, default {all}

use -o  to choose the output directory name, default {trimeshkit_build}

Example:
build Windows Win64 debug

    git bash> ./build.sh -t windows -a Win64 -b debug


### Output
the output libraries is in TriMeshKit/bin
