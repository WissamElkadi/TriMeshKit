# Project
project(Modules)

# Modules
add_subdirectory("mesh_processing")

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_subdirectory("java")
endif()

set_property(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} PROPERTY VS_STARTUP_PROJECT mesh_processing)
