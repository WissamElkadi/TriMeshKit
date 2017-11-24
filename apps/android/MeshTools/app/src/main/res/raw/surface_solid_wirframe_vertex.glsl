#version 310 es

//Input
in vec3 v_Position;
in vec3 v_Normal;

//Uniform
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;

//Output
out vec3 g_Normal;
out vec3 g_Position;

void main()
{
	gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(v_Position, 1.0);

	g_Normal = vec3(u_ModelViewMatrix * vec4(v_Normal, 0.0));
	g_Position = vec3(u_ModelViewMatrix * vec4(v_Position, 1.0));
}