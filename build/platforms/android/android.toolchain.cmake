set(CMAKE_SYSTEM_NAME Android)
set(SELECTED_PLATFORM "android")
set(BUILD_ANDROID true)
set(CMAKE_ANDROID_NDK_TOOLCHAIN_VERSION clang)

set(ANDROID_CPP_FEATURES "rtti exceptions")

set(ANDROID_NATIVE_API_LEVEL 16)
set(ANDROID_STL c++_static)

set(TRIMESHKIT_PLATFORM_ANDROID 1)

include($ENV{ANDROID_NDK}/build/cmake/android.toolchain.cmake)