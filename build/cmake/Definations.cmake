#3rdparty Libs
set(OPENMESH_ROOT ${ROOT_DIR}/3rdparty/open_mesh)
set(EIGEN_ROOT ${ROOT_DIR}/3rdparty/eigen_lib)
set(MODULES_ROOT ${ROOT_DIR}/modules)

# Libs Names
set(TARGET_MESH_PROCESSING_LIB_NAME "TriMeshProcessing")
set(TARGET_OPEN_MESH_CORE_LIB_NAME "OpenMeshCore")
set(TARGET_TRI_MESH_KIT_LIB_NAME "TriMeshKit")
set(TARGET_TRI_MESH_KIT_JNI_LIB_NAME "TriMeshKit-JNI")
set(TARGET_TRI_MESH_KIT_JAVA_LIB_NAME "TriMeshKit-java")


# PACKAGES DIRECTORY
set(TRIMESHKIT_INSTALL_DIRECTORY    ${ROOT_DIR}/install)

# INTERNAL PACKAGES
set(TRIMESHKIT_INTERNAL_PACKAGE_DIRECTORY    ${TRIMESHKIT_INSTALL_DIRECTORY}/${SELECTED_PLATFORM}/Internal)

mark_as_advanced(TRIMESHKIT_INSTALL_DIRECTORY)