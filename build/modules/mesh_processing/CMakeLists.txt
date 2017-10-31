project(MeshProcessing)

set(LIB_DIR ${ROOT_DIR}/modules/mesh_processing)

# Source Files
set(LIB_SRC
    ${LIB_DIR}/src/TriMeshUtils.cpp
    ${LIB_DIR}/src/TriMesh.cpp
)

# Include Files
set(LIB_INC
    ${LIB_DIR}/include/TriMeshUtils.h
    ${LIB_DIR}/include/TriMesh.h
)

# Source files grouping in IDE
source_group("include" FILES ${LIB_INC})
source_group("src" FILES ${LIB_SRC})

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_library(${TARGET_MESH_PROCESSING_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})

    target_link_libraries(${TARGET_MESH_PROCESSING_LIB_NAME}
                          ${TARGET_OPEN_MESH_CORE_LIB_NAME}
                 )

elseif(TRIMESHKIT_PLATFORM_WINDOWS)
    add_library(${TARGET_MESH_PROCESSING_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})

    add_library(OpenMeshCoreLib SHARED IMPORTED)
    set_property(TARGET OpenMeshCoreLib PROPERTY IMPORTED_LOCATION ${TARGET_OPEN_MESH_CORE_LIB_NAME})
    set_property(TARGET OpenMeshCoreLib PROPERTY IMPORTED_IMPLIB ${TARGET_OPEN_MESH_CORE_LIB_NAME})

    target_link_libraries(${TARGET_MESH_PROCESSING_LIB_NAME}
	                  OpenMeshCoreLib
                 )
endif()

target_include_directories(${TARGET_MESH_PROCESSING_LIB_NAME}
                     PUBLIC
					 ${LIB_DIR}/include
                     ${OPENMESH_ROOT}/src
                     ${EIGEN_ROOT}
                    )

installAndExportLib(${TARGET_MESH_PROCESSING_LIB_NAME})
