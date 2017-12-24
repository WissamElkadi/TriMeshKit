project(Triangle)

set(LIB_DIR ${TRIANGLE_ROOT})

# Source Files
set(LIB_SRC
    ${LIB_DIR}/triangle.c
)

# Include Files
set(LIB_INC
    ${LIB_DIR}/triangle.h
)

# Source files grouping in IDE
source_group("include" FILES ${LIB_INC})
source_group("src" FILES ${LIB_SRC})

add_definitions(
    -DANSI_DECLARATORS
	  -DTRILIBRARY
)

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_library(${TARGET_TRIANGLE_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})
elseif(TRIMESHKIT_PLATFORM_WINDOWS)
    add_library(${TARGET_TRIANGLE_LIB_NAME}  ${LIB_SRC} ${LIB_INC})
endif()

target_compile_definitions(${TARGET_TRIANGLE_LIB_NAME} PRIVATE  -DTRILIBRARY -DANSI_DECLARATORS -DNO_TIMER)

target_include_directories(${TARGET_TRIANGLE_LIB_NAME}
                     PUBLIC
                     ${LIB_DIR}
                    )

installAndExportLib(${TARGET_TRIANGLE_LIB_NAME})
