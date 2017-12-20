# Project
project(Modules)

# Modules
add_subdirectory("mesh_processing")

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_subdirectory("java")
endif()