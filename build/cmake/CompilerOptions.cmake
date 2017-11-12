macro(set_compiler_option)

    if(TRIMESHKIT_PLATFORM_ANDROID)
        set(CMAKE_CONFIGURATION_TYPES "${OPENAR_BUILD_FLAVOR_DEBUG};${OPENAR_BUILD_FLAVOR_RELEASE}" CACHE INTERNAL "Configurations types for android")

        add_compile_options(-Qunused-arguments)

        string(REPLACE "-fno-exceptions" "-fexceptions" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
        string(REPLACE "-fno-exceptions" "-fexceptions" CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")

        string(REPLACE "-Os" "-O2" CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE}")
        string(REPLACE "-Os" "-O2" CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE}")

        add_compile_options(-fomit-frame-pointer
                            -fno-strict-aliasing
                            -fsigned-char)

    elseif(TRIMESHKIT_PLATFORM_WINDOWS)
	endif()
	
endmacro()