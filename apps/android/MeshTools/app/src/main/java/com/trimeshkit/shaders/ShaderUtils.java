package com.trimeshkit.shaders;

import android.content.Context;
import android.opengl.GLES31;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wahmed on 21/10/2017.
 */

public class ShaderUtils {

    public static int loadShader(int _shaderType, String _shaderCode) {
        // create a vertex shader type (GLES31.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
        int shader = GLES31.glCreateShader(_shaderType);

        // add the source code to the shader and compile it
        GLES31.glShaderSource(shader, _shaderCode);
        GLES31.glCompileShader(shader);

        int[] compiled = new int[1];

        GLES31.glGetShaderiv(shader, GLES31.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e("ShaderUtils", "Couldn't compile shader " + _shaderType + ":");
            Log.e("ShaderUtils", GLES31.glGetShaderInfoLog(shader));
            GLES31.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    public static int createProgram(int _vertexShader, int _fragmentShader, int _geometryShader) {
        int program = GLES31.glCreateProgram();
        if (program == 0)
            return 0;

        GLES31.glAttachShader(program, _vertexShader);

        if (_geometryShader != -1)
            GLES31.glAttachShader(program, _geometryShader);

        GLES31.glAttachShader(program, _fragmentShader);

        GLES31.glLinkProgram(program);

        int[] linked = new int[1];
        GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e("ShaderUtils", "Error linking program");
            Log.e("ShaderUtils", GLES31.glGetProgramInfoLog(program));
            GLES31.glDeleteProgram(program);
            program = 0;
        }

        return program;
    }

    public static void checkGLError(String op) {
        for (int error = GLES31.glGetError(); error != 0; error = GLES31.glGetError())
            Log.e("ShaderUtils", "After operation " + op + " got glError 0x"
                    + Integer.toHexString(error));
    }

    public static String readShaderSourceCode(Context _context, int rawFileIndex) {
        InputStream shaderInputStream = _context.getResources().openRawResource(rawFileIndex);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = shaderInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            shaderInputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

}
