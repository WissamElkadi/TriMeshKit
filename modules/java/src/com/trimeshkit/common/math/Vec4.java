package com.trimeshkit.math;

public class Vec4 extends Vector<Float> {
    public Vec4() {
        super();
        mData = new Float[4];
        for (float elem : mData) {
            elem = 0.0f;
        }
    }
}