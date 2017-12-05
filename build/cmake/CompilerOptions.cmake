macro(set_compiler_option)

    if(TRIMESHKIT_PLATFORM_ANDROID)
        set(CMAKE_CONFIGURATION_TYPES "Debug;Release")

        add_compile_options(-Qunused-arguments)
		
	    add_compile_options($<$<COMPILE_LANGUAGE:CXX>:-std=c++1z>)
	    add_compile_options($<$<COMPILE_LANGUAGE:CXX>:-stdlib=libc++>)

	    set(CMAKE_CXX_FLAGS "-fopenmp=libomp ${CMAKE_CXX_FLAGS}")

		string(REPLACE "-std=c++11" "-std=c++1z" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")		
        string(REPLACE "-fno-exceptions" "-fexceptions" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")


    elseif(TRIMESHKIT_PLATFORM_WINDOWS)
	endif()
	
endmacro()