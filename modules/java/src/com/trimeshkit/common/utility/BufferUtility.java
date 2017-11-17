package com.trimeshkit.utility;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferUtility {
	public static Buffer getIntBuffer(int[] array) {
		// Each int takes 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((Integer.SIZE / Byte.SIZE) * array.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        for (int s : array)
            byteBuffer.putInt(s);

        byteBuffer.rewind();

        return byteBuffer;
	}

	public static Buffer getFloatBuffer(float[] array) {
		// Each float takes 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((Float.SIZE / Byte.SIZE) * array.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        for (float d : array)
            byteBuffer.putFloat(d);
        
        byteBuffer.rewind();

        return byteBuffer;
	}
}
