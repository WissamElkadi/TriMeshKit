#include "CurveUtils.h"
#include "CurvatureFlowSmoother.h"
#include "Curve.h"

bool TriMeshKit::CurveProcessing::CurveUtils::smooth(Curve& _curve)
{
    CurvatureFlowSmoother smoother(_curve);
    return smoother.smoothCurve();
}

bool TriMeshKit::CurveProcessing::CurveUtils::uniformResample(Curve& _curve, float _length)
{
    auto targetCount = _curve.euclideanLength() / _length;
    _curve.uniformSample(targetCount);

    return true;
}
