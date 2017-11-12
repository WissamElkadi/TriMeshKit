# Apps
project(Apps)

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_subdirectory(android)
elseif(TRIMESHKIT_PLATFORM_WINDOWS)
    add_subdirectory(windows)
endif()