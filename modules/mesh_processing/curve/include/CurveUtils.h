#ifndef Curve_PROCESSING_CURVEUTILS_H
#define Curve_PROCESSING_CURVEUTILS_H

namespace TriMeshKit
{
    namespace CurveProcessing
    {
        class Curve;

        class CurveUtils
        {
        public:

            static bool smooth(Curve& _curve);
            static bool uniformResample(Curve& _curve, float _length);
        };
    }
}

#endif // Curve_PROCESSING_CURVEUTILS_H
