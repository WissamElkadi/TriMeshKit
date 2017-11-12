project(TriMeshKitJava)

set(LIB_DIR ${ROOT_DIR}/modules/java)

# Source Files
set(LIB_SRC
    ${LIB_DIR}/src/MeshToolsUtils.cpp
)

# Include Files


# Source files grouping in IDE
source_group("src" FILES ${LIB_SRC})

# if(TRIMESHKIT_PLATFORM_ANDROID)
    add_library(${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME} SHARED ${LIB_SRC})

    target_link_libraries(${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME}
	                      ${TARGET_MESH_PROCESSING_LIB_NAME}
                          ${TARGET_OPEN_MESH_CORE_LIB_NAME}
						  )

# else()
    # message(WARNING "java is not supposed to build for this platform")
    # return()
# endif()

target_include_directories(${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME}
                     PUBLIC
					 ${LIB_DIR}/include
                     ${OPENMESH_ROOT}/src
                     ${MODULES_ROOT}/mesh_processing/include
                    )

installAndExportLib(${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME})
