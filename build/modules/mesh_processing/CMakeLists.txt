project(MeshProcessing)

set(LIB_DIR ${ROOT_DIR}/modules/mesh_processing)

# Source Files
set(LIB_MESH_SRC
    ${LIB_DIR}/mesh/src/TriMeshUtils.cpp
    ${LIB_DIR}/mesh/src/TriMesh.cpp
    ${LIB_DIR}/mesh/src/LaplaceBeltramiOperator.cpp
    ${LIB_DIR}/mesh/src/TriMeshLinearSystem.cpp
    ${LIB_DIR}/mesh/src/EigenUtils.cpp
    ${LIB_DIR}/mesh/src/EigenUtilsTemp.cpp
    ${LIB_DIR}/mesh/src/TriMeshAlgorithms.cpp
)

set(LIB_CURVE_SRC
    ${LIB_DIR}/curve/src/Curve.cpp
    ${LIB_DIR}/curve/src/CurveUtils.cpp
    ${LIB_DIR}/curve/src/CurvatureFlowSmoother.cpp
)

set(LIB_SRC
    ${LIB_MESH_SRC}
    ${LIB_CURVE_SRC}
)

# Include Files
set(LIB_MESH_INC
    ${LIB_DIR}/mesh/include/TriMeshUtils.h
    ${LIB_DIR}/mesh/include/TriMesh.h
    ${LIB_DIR}/mesh/include/LaplaceBeltramiOperator.h
    ${LIB_DIR}/mesh/include/TriMeshLinearSystem.h
    ${LIB_DIR}/mesh/include/EigenUtils.h
    ${LIB_DIR}/mesh/include/TriMeshAlgorithms.h
)

set(LIB_CURVE_INC
    ${LIB_DIR}/curve/include/Curve.h
    ${LIB_DIR}/curve/include/CurveUtils.h
    ${LIB_DIR}/curve/include/CurvatureFlowSmoother.h
)

set(LIB_INC
    ${LIB_MESH_INC}
    ${LIB_CURVE_INC}
)

# Source files grouping in IDE

source_group(mesh\\include FILES ${LIB_MESH_INC})
source_group(mesh\\src FILES ${LIB_MESH_SRC})

source_group(curve\\include FILES ${LIB_CURVE_INC})
source_group(curve\\src FILES ${LIB_CURVE_SRC})

if(TRIMESHKIT_PLATFORM_ANDROID)

    add_library(${TARGET_MESH_PROCESSING_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})

elseif(TRIMESHKIT_PLATFORM_WINDOWS)

    add_definitions(
        -D_USE_MATH_DEFINES
        )

    link_directories(${ROOT_DIR}/bin/${SELECTED_PLATFORM}/${TARGET_ARCH}
                )

    add_library(${TARGET_MESH_PROCESSING_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})

    add_library(OpenMeshCoreLib SHARED IMPORTED)
    set_property(TARGET OpenMeshCoreLib PROPERTY IMPORTED_LOCATION ${TARGET_OPEN_MESH_CORE_LIB_NAME})
    set_property(TARGET OpenMeshCoreLib PROPERTY IMPORTED_IMPLIB ${TARGET_OPEN_MESH_CORE_LIB_NAME})

endif()

target_link_libraries(${TARGET_MESH_PROCESSING_LIB_NAME}
                      ${TARGET_OPEN_MESH_CORE_LIB_NAME}
                      ${TARGET_TRIANGLE_LIB_NAME}
                )

target_include_directories(${TARGET_MESH_PROCESSING_LIB_NAME}
                     PUBLIC
                     ${LIB_DIR}/mesh/include
                     ${LIB_DIR}/curve/include
                     ${OPENMESH_ROOT}/src
                     ${EIGEN_ROOT}
                     ${TRIANGLE_ROOT}
                    )

installAndExportLib(${TARGET_MESH_PROCESSING_LIB_NAME})
