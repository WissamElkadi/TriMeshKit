project(OpenMeshCore)

set(LIB_DIR ${OPENMESH_ROOT})

# source code directories
set (directories 
  ${LIB_DIR}/src/OpenMesh/Core/Geometry 
  ${LIB_DIR}/src/OpenMesh/Core/IO 
  ${LIB_DIR}/src/OpenMesh/Core/IO/exporter 
  ${LIB_DIR}/src/OpenMesh/Core/IO/importer 
  ${LIB_DIR}/src/OpenMesh/Core/IO/reader 
  ${LIB_DIR}/src/OpenMesh/Core/IO/writer
  ${LIB_DIR}/src/OpenMesh/Core/Mesh 
  ${LIB_DIR}/src/OpenMesh/Core/Mesh/gen 
  ${LIB_DIR}/src/OpenMesh/Core/System 
  ${LIB_DIR}/src/OpenMesh/Core/Utils
  )

add_definitions(
    -D_USE_MATH_DEFINES -DNOMINMAX
    -D_CRT_SECURE_NO_WARNINGS
	)

# collect all header and source files
append_files (LIB_INC "*.hh" ${directories})
append_files (LIB_SRC "*.cc" ${directories})

#Drop the template only cc files
#drop_templates(LIB_SRC)

if(TRIMESHKIT_PLATFORM_ANDROID)
    add_library(${TARGET_OPEN_MESH_CORE_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})
elseif(TRIMESHKIT_PLATFORM_WINDOWS)
    add_library(${TARGET_OPEN_MESH_CORE_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})
endif()

target_include_directories(${TARGET_OPEN_MESH_CORE_LIB_NAME}
                     PUBLIC
                     ${LIB_DIR}/src
                    )

installAndExportLib(${TARGET_OPEN_MESH_CORE_LIB_NAME})
