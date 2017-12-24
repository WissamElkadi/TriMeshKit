project(TriMeshKit)
cmake_minimum_required(VERSION 3.6.0)
mark_as_advanced(CMAKE_TOOLCHAIN_FILE)

# set the root dir
get_filename_component(ROOT_DIR "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)

# Set output and install locations
message(STATUS "CMAKE_BINARY_DIR: " ${CMAKE_BINARY_DIR})

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)

# Commands like include() and find_package() search for files in directories
# listed by this variable before checking the default modules that come with CMake.
set(CMAKE_MODULE_PATH
    ${CMAKE_MODULE_PATH}
    ${ROOT_DIR}/build/cmake
    ${ROOT_DIR}/build/platforms)

# Check if toolchain specified
if(NOT TRIMESHKIT_PLATFORM_WINDOWS AND NOT TRIMESHKIT_PLATFORM_ANDROID)
    message(FATAL_ERROR "No toolchain file set")
endif()

# set Options to user
option(BUILD_APPS "Build Apps" OFF)

# Include macros and definations
include(Definations)
include(CompilerOptions)
include(TriMeshKitMacros)

#Set Compiler Options
set_compiler_option()

# Add module subdirectory
add_subdirectory("modules")

#Add 3rdParty subdirectory
add_subdirectory("3rdparty")

if(BUILD_APPS)
    add_subdirectory("apps")
endif()
