#include "Curve.h"

TriMeshKit::CurveProcessing::Curve::Curve(const std::vector<OpenMesh::Vec3d>& _points, bool _isClosed/* = false*/) :
    mPoints(_points),
    mIsClosed(_isClosed)
{

}

TriMeshKit::CurveProcessing::Curve::Curve(const std::vector<OpenMesh::Vec2d>& _points, bool _isClosed/* = false*/) :
    mIsClosed(_isClosed)
{
    for (const auto& point : _points)
    {
        mPoints.push_back(OpenMesh::Vec3d(point[0], point[1], 0.0));
    }
}

double TriMeshKit::CurveProcessing::Curve::euclideanLength()
{
    auto start = mPoints.begin();

    if (mPoints.size() < 2)
        return 0;

    auto finish = start + 1;
    double sum = 0;
    while (finish != mPoints.end())
    {
        sum += (*finish - *start).length();
        start = finish++;
    }

    if (mIsClosed)
    {
        finish = mPoints.begin();
        sum += (*finish - *start).length();
    }

    return sum;
}

size_t TriMeshKit::CurveProcessing::Curve::size()
{
    return mPoints.size();
}

bool TriMeshKit::CurveProcessing::Curve::isClosed()
{
    return mIsClosed;
}

void TriMeshKit::CurveProcessing::Curve::updatePoint(int _pointIndex, OpenMesh::Vec3d _newPoint)
{
    mPoints.at(_pointIndex) = _newPoint;
}

void TriMeshKit::CurveProcessing::Curve::uniformSample(size_t _targetCount)
{
    if (mPoints.size() < 2 || _targetCount < 2) {
        // degenerate source vector or target_count value
        // for simplicity, this returns an empty result
        // but special cases may be handled when appropriate for the application
        return;
    }

    std::vector<OpenMesh::Vec3d> result;

    // segment_length is the length between result points, taken as
    // distance traveled between these points on a linear interpolation
    // of the source points.  The actual Euclidean distance between
    // points in the result vector can vary, and is always less than
    // or equal to segment_length.
    const double segment_length = euclideanLength() / (_targetCount - 1);

    // start and finish are the current source segment's endpoints
    auto start = mPoints.begin();
    auto finish = start + 1;

    // src_segment_offset is the distance along a linear interpolation
    // of the source curve from its first point to the start of the current
    // source segment.
    double src_segment_offset = 0;

    // src_segment_length is the length of a line connecting the current
    // source segment's start and finish points.
    double src_segment_length = (*finish - *start).length();

    // The first point in the result is the same as the first point
    // in the source.
    result.push_back(*start);

    for (std::size_t i = 1; i < _targetCount - 1; ++i) {
        // next_offset is the distance along a linear interpolation
        // of the source curve from its beginning to the location
        // of the i'th point in the result.
        // segment_length is multiplied by i here because iteratively
        // adding segment_length could accumulate error.
        const double next_offset = segment_length * i;

        // Check if next_offset lies inside the current source segment.
        // If not, move to the next source segment and update the
        // source segment offset and length variables.
        while (src_segment_offset + src_segment_length < next_offset) {
            src_segment_offset += src_segment_length;
            start = finish++;
            src_segment_length = (*finish - *start).length();
        }
        // part_offset is the distance into the current source segment
        // associated with the i'th point's offset.
        const double part_offset = next_offset - src_segment_offset;

        // part_ratio is part_offset's normalized distance into the 
        // source segment. Its value is between 0 and 1,
        // where 0 locates the next point at "start" and 1
        // locates it at "finish".  In-between values represent a
        // weighted location between these two extremes.
        const double part_ratio = part_offset / src_segment_length;

        // Use part_ratio to calculate the next point's components
        // as weighted averages of components of the current
        // source segment's points.
        result.push_back(OpenMesh::Vec3d(
            (*start)[0] + part_ratio * ((*finish)[0] - (*start)[0]),
            (*start)[1] + part_ratio * ((*finish)[1] - (*start)[1]),
            (*start)[2] + part_ratio * ((*finish)[2] - (*start)[2])
        ));
    }

    // The first and last points of the result are exactly
    // the same as the first and last points from the input,
    // so the iterated calculation above skips calculating
    // the last point in the result, which is instead copied
    // directly from the source vector here.
    if (!mIsClosed)
        result.push_back(mPoints.back());

    mPoints.clear();
    mPoints = result;
}

std::vector<OpenMesh::Vec2d> TriMeshKit::CurveProcessing::Curve::getCurve2DPoints() const
{
    std::vector<OpenMesh::Vec2d> result;
    for (const auto& point : mPoints)
    {
        result.push_back(OpenMesh::Vec2d(point[0], point[1]));
    }

    return result;
}

std::vector<OpenMesh::Vec3d> TriMeshKit::CurveProcessing::Curve::getCurvePoints() const
{
    return mPoints;
}
