# append all files with extension "ext" in the "dirs" directories to "ret"
# excludes all files starting with a '.' (dot)
macro (append_files ret ext)
  foreach (_dir ${ARGN})
    file (GLOB _files "${_dir}/${ext}")
    foreach (_file ${_files})
      get_filename_component (_filename ${_file} NAME)
      if (_filename MATCHES "^[.]")
	    list (REMOVE_ITEM _files ${_file})
      endif ()
    endforeach ()
    list (APPEND ${ret} ${_files})
  endforeach ()
endmacro ()

# drop all "*T.cc" files from list
macro (drop_templates list)
  foreach (_file ${${list}})
    if (_file MATCHES "T.cc$")
      list (REMOVE_ITEM ${list} ${_file})
    endif ()
  endforeach ()
endmacro ()


# install and copy lib file to the repo
macro (installAndExportLib libName)
    # Install rules.
    install(TARGETS ${libName}
        RUNTIME DESTINATION ${TRIMESHKIT_INTERNAL_PACKAGE_DIRECTORY}/bin/${TARGET_ARCH}
        LIBRARY DESTINATION ${TRIMESHKIT_INTERNAL_PACKAGE_DIRECTORY}/lib/${TARGET_ARCH}
        ARCHIVE DESTINATION ${TRIMESHKIT_INTERNAL_PACKAGE_DIRECTORY}/lib/${TARGET_ARCH}
    	)
    
    add_custom_command(TARGET  ${libName} POST_BUILD
                       DEPENDS "${ROOT_DIR}/bin/${CMAKE_BUILD_TYPE}/${SELECTED_PLATFORM}/${TARGET_ARCH}/$<TARGET_FILE_NAME:${TARGET_OPEN_MESH_CORE_LIB_NAME}>"
                       COMMAND ${CMAKE_COMMAND} -E copy
                       $<TARGET_FILE:${libName}>
                       ${ROOT_DIR}/bin/${SELECTED_PLATFORM}/${CMAKE_BUILD_TYPE}/${TARGET_ARCH}/$<TARGET_FILE_NAME:${libName}>
    				   )
endmacro ()
