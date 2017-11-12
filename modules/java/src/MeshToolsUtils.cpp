#include <jni.h>
#include <string>
#include <vector>
#include <TriMesh.h>
#include <TriMeshUtils.h>

#include <fstream>
#include <cstring>

#ifdef __cplusplus
extern "C"
{
#endif

using namespace TriMeshKit::MeshProcessing;
TriMesh mainMesh;


JNIEXPORT bool JNICALL
Java_com_trimeshkit_NativeTriMeshKit_loadMesh(JNIEnv* _env, jobject, jstring _path)
{
    const char *nativeString = _env->GetStringUTFChars( _path, 0);

    auto result = TriMeshUtils::readMesh( mainMesh, nativeString);

    _env->ReleaseStringUTFChars(_path, nativeString);

    return result;
}

JNIEXPORT jfloatArray JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getVerticesPoints(JNIEnv* _env, jobject)
{
    auto pointsVector = mainMesh.getVerticesPoints();

    std::vector<jfloat> pointsJavaVector;

    for (auto const& coordinate : pointsVector) {
        pointsJavaVector.push_back(coordinate);
    }

    jfloatArray result;
    result = _env->NewFloatArray(pointsVector.size());
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // move from the temp structure to the java structure
    _env->SetFloatArrayRegion(result, 0, pointsJavaVector.size(), pointsJavaVector.data());
    return result;
}

JNIEXPORT jfloatArray JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getVerticesNormals(JNIEnv* _env, jobject)
{
    auto normalsVector = mainMesh.getVerticesNormals();

    std::vector<jfloat> normalsVectorJavaVector;

    for (auto const& normal : normalsVector) {
        normalsVectorJavaVector.push_back(normal);
    }

    jfloatArray result;
    result = _env->NewFloatArray(normalsVector.size());
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // move from the temp structure to the java structure
    _env->SetFloatArrayRegion(result, 0, normalsVectorJavaVector.size(), normalsVectorJavaVector.data());
    return result;
}

JNIEXPORT jintArray JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getFacesIndices(JNIEnv* _env, jobject)
{
    auto facesIndices = mainMesh.getFacesIndices();

    std::vector<jint> facesIndicesJavaVector;

    for (auto const& vertexIndex : facesIndices) {
        facesIndicesJavaVector.push_back(vertexIndex);
    }

    jintArray result;
    result = _env->NewIntArray(facesIndices.size());
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // move from the temp structure to the java structure
    _env->SetIntArrayRegion(result, 0, facesIndicesJavaVector.size(), facesIndicesJavaVector.data());
    return result;
}

JNIEXPORT jint JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getNumVertexCoordinates(JNIEnv* _env, jobject)
{
    return mainMesh.point(mainMesh.vertex_handle(0)).size();
}

JNIEXPORT jint JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getNumberOfFaces(JNIEnv* _env, jobject)
{
    return mainMesh.n_faces();
}

JNIEXPORT jfloatArray JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getCenter(JNIEnv* _env, jobject)
{
    auto centerPoint = mainMesh.getCenter();

    jfloatArray result;
    result = _env->NewFloatArray(3);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // move from the temp structure to the java structure
    _env->SetFloatArrayRegion(result, 0, centerPoint.size(), centerPoint.data());
    return result;
}

JNIEXPORT jfloatArray JNICALL
Java_com_trimeshkit_NativeTriMeshKit_getBoundingBox(JNIEnv* _env, jobject)
{
    auto boundingBox = mainMesh.getBoundingBox();

    jfloatArray result;
    result = _env->NewFloatArray(6);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }

    // move from the temp structure to the java structure
    _env->SetFloatArrayRegion(result, 0, boundingBox.at(0).size(), boundingBox.at(0).data());
    _env->SetFloatArrayRegion(result, 3, boundingBox.at(1).size(), boundingBox.at(1).data());

    return result;
}

#ifdef __cplusplus
}
#endif
