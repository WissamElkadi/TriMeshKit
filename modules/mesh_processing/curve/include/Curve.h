#ifndef Curve_PROCESSING_CURVE_H
#define Curve_PROCESSING_CURVE_H

#include <vector>
#include "OpenMesh/Core/Geometry/Vector11T.hh"

namespace TriMeshKit
{
    namespace CurveProcessing
    {
        class Curve
        {
        public:
            Curve(const std::vector<OpenMesh::Vec3d>& _points, bool _isClosed = false);
            Curve(const std::vector<OpenMesh::Vec2d>& _points, bool _isClosed = false);

            double euclideanLength();
            size_t size();
            bool isClosed();
            void updatePoint(int _pointIndex, OpenMesh::Vec3d _newPoint);
            void uniformSample(size_t _targetCount);

            std::vector<OpenMesh::Vec2d>  getCurve2DPoints() const;
            std::vector<OpenMesh::Vec3d>  getCurvePoints() const;

        private:
            std::vector<OpenMesh::Vec3d>     mPoints;
            bool                             mIsClosed;
        };
    }
}

#endif // Curve_PROCESSING_CURVEUTILS_H
