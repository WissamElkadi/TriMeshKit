project(TriMeshKitJava)

set(LIB_DIR ${ROOT_DIR}/modules/java)

# Source Files
set(LIB_SRC
	${LIB_DIR}/src/NativeHandle.cpp  
    ${LIB_DIR}/src/meshprocessing/com_trimeshkit_meshprocessing_TriMesh.cpp
	${LIB_DIR}/src/meshprocessing/com_trimeshkit_meshprocessing_TriMeshUtils.cpp
)

# Include Files
# Include Files
set(LIB_INC
    ${LIB_DIR}/include/NativeHandle.h  
)

# Source files grouping in IDE
source_group("src" FILES ${LIB_SRC})

# if(TRIMESHKIT_PLATFORM_ANDROID)
    add_library(${TARGET_TRI_MESH_KIT_JNI_LIB_NAME} SHARED ${LIB_SRC} ${LIB_INC})

    target_link_libraries(${TARGET_TRI_MESH_KIT_JNI_LIB_NAME}
	                      ${TARGET_MESH_PROCESSING_LIB_NAME}
                          ${TARGET_OPEN_MESH_CORE_LIB_NAME}
						  )

# else()
    # message(WARNING "java is not supposed to build for this platform")
    # return()
# endif()

target_include_directories(${TARGET_TRI_MESH_KIT_JNI_LIB_NAME}
                     PUBLIC
					 ${LIB_DIR}/include
                     ${OPENMESH_ROOT}/src
                     ${MODULES_ROOT}/mesh_processing/include
                    )

installAndExportLib(${TARGET_TRI_MESH_KIT_JNI_LIB_NAME})

######################################## Create JAR #####################################################

find_package(Java REQUIRED)
include(UseJava)

set(JAVA_PACKAGE_DIR ${ROOT_DIR}/modules/java/src/com/trimeshkit)

set(JAVA_SRC
	${JAVA_PACKAGE_DIR}/common/math/Vector.java 
    ${JAVA_PACKAGE_DIR}/common/math/Vec3.java 
    ${JAVA_PACKAGE_DIR}/common/math/Vec4.java 
    ${JAVA_PACKAGE_DIR}/common/utility/BufferUtility.java	
    ${JAVA_PACKAGE_DIR}/meshprocessing/TriMesh.java
	${JAVA_PACKAGE_DIR}/meshprocessing/TriMeshUtils.java
)

add_jar(${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME} ${JAVA_SRC})

add_custom_command(TARGET  ${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME} POST_BUILD
                            COMMAND ${CMAKE_COMMAND} -E copy
                            ${CMAKE_BINARY_DIR}/modules/java/${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME}.jar
                            ${ROOT_DIR}/bin/${SELECTED_PLATFORM}/${TARGET_TRI_MESH_KIT_JAVA_LIB_NAME}.jar
                          )
						  