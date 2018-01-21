#ifndef Curve_PROCESSING_CURVATUREFLOWDMOOTHER_H
#define Curve_PROCESSING_CURVATUREFLOWDMOOTHER_H

#include <vector>
#include <OpenMesh/Core/Geometry/VectorT.hh>

//**********************************************************************************\\
// The basic idea is captured by the heat equation, which describes the way heat	\\
// diffuses over a domain. For instance, if u is a scalar function describing the	\\
// temperature at every point on the real line, then the heat equation is given by	\\
// ∂u/∂t = ∂2u/∂x2.																	\\
// Geometrically this equation simply says that concave bumps get pushed down and	\\
// convex bumps get pushed up.														\\
// after a long time the heat distribution becomes completely flat.					\\
// We also could have written this equation using the Laplacian: ∂f / ∂t = ∆f 		\\
// Discretized the equation ∂f / ∂t ≈ (fh − f0) / h									\\
// So heat Equation will be (fh − f0) / h= ∆f .										\\
// The only remaining question is: which values of f do we use on the right-hand	\\
// side?																			\\
// We will use Backward Euler Schema to set the value of f to fh which leading to	\\
// the System , leading to the system (I − h∆) fh = f0								\\
// Fortunately this system is highly sparse, which means it is not too expensive to	\\
// solve in practice, we will use Eigen Library and LDLT Factorization				\\
// the function here is Curve points Coordinates									\\
//**********************************************************************************\\

namespace TriMeshKit
{
    namespace CurveProcessing
    {
        class Curve;
        class CurvatureFlowSmoother
        {
        public:
            CurvatureFlowSmoother(Curve& _curve);
            void setTimeStep(double _timeStep);
            double getTimeStep() const;
            bool smoothCurve();

        private:
            double                          mTimeStep;
            Curve&                           mCurve;
        };
    }
}

#endif //Curve_PROCESSING_CURVATUREFLOWDMOOTHER_H
