package com.trimeshkit.math;

/**
 * Created by wahmed on 15/11/2017.
 */

public abstract class Vector<E> {
    protected E[] mData;
    public E get(int i)
    {
        return mData[i];
    }

    public int size()
    {
        return mData.length;
    }
}