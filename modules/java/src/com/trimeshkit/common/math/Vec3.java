package com.trimeshkit.math;

public class Vec3 extends Vector<Float> {
	public Vec3() {
		super();
		mData = new Float[3];
		for (float elem : mData) {
			elem = 0.0f;
		}
	}
}