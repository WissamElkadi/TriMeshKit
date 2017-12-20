/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_trimeshkit_meshprocessing_TriMeshAlgorithms */

#ifndef _Included_com_trimeshkit_meshprocessing_TriMeshAlgorithms
#define _Included_com_trimeshkit_meshprocessing_TriMeshAlgorithms

#include <TriMeshAlgorithms.h>
#include <TriMesh.h>
#include <NativeHandle.h>

#ifdef __cplusplus
extern "C" {
#endif
using namespace TriMeshKit::MeshProcessing;

/*
 * Class:     com_trimeshkit_meshprocessing_TriMeshAlgorithms
 * Method:    smooth
 * Signature: (Lcom/trimeshkit/meshprocessing/TriMesh;)Z
 */
JNIEXPORT bool JNICALL Java_com_trimeshkit_meshprocessing_TriMeshAlgorithms_smooth
  (JNIEnv * _env, jclass _class, jobject _mesh)
  {
	  TriMesh *inst = getHandle<TriMesh>(_env, _mesh);

      return TriMeshAlgorithms::smooth( *inst);

  }

 JNIEXPORT void JNICALL Java_com_trimeshkit_meshprocessing_TriMeshAlgorithms_triangulate
 (JNIEnv * _env, jclass _class, jobject _mesh, jdoubleArray _pointList)
 {
  TriMesh *inst = getHandle<TriMesh>(_env, _mesh);
  
  jsize pointsLength = _env->GetArrayLength(_pointList);
  std::vector<OpenMesh::Vec2d> pointsVector;
  
  jdouble *_pointArray = _env->GetDoubleArrayElements(_pointList, 0);
  
     for (int i=0; i<pointsLength;) {
	  double x = _pointArray[i];
	  i++;
	  double y = _pointArray[i];
	  i++;
	  OpenMesh::Vec2d point(x, y);
         pointsVector.push_back(point);
     }
	  
  std::vector<OpenMesh::Vec2ui> segmentList;
  
  int k;
  for(int i = 0; i < pointsVector.size(); ++i)
  {
	  k = i;
	  OpenMesh::Vec2ui segment(k, (k+1) % pointsVector.size());
	  segmentList.push_back(segment);
  }

     TriMeshAlgorithms::triangulate( *inst, pointsVector, segmentList, std::vector<OpenMesh::Vec2d>(), 
                                   std::vector<int>(), std::vector<int>(), "a0.001q");

  _env->ReleaseDoubleArrayElements(_pointList, _pointArray, 0);
 }

#ifdef __cplusplus
}
#endif
#endif