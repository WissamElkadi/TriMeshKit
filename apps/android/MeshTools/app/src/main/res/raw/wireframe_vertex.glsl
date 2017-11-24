#version 310 es

//Input
in vec3 v_Position;

//Uniform
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;


void main()
{
	gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(v_Position, 1.0);
}