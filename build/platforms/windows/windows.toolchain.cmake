set(SELECTED_PLATFORM "windows")

if(NOT CMAKE_GENERATOR MATCHES "^Visual Studio")
	message (FATAL_ERROR "Unsupported CMAKE_GENERATOR value selected: ${CMAKE_GENERATOR}")
endif()

if(${CMAKE_GENERATOR} MATCHES "Win64$")
    set(TARGET_ARCH "x86_64")
else()
    set(TARGET_ARCH "i386")
endif()

set(TRIMESHKIT_PLATFORM_WINDOWS 1)
