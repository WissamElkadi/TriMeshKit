#version 310 es

#extension GL_EXT_geometry_shader : enable

layout(triangles) in;
layout(triangle_strip) out;
layout(max_vertices = 3) out;

uniform vec2 u_ScreenSize;
in vec3 g_Normal[];
in vec3 g_Position[];

out vec3 f_Normal;
out vec3 f_Position;
out vec3 f_DistanceVector;

void main()
{
	vec2 p0 = u_ScreenSize * gl_in[0].gl_Position.xy/gl_in[0].gl_Position.w;
	vec2 p1 = u_ScreenSize * gl_in[1].gl_Position.xy/gl_in[1].gl_Position.w;
	vec2 p2 = u_ScreenSize * gl_in[2].gl_Position.xy/gl_in[2].gl_Position.w;
	vec2 v0 = p2-p1;
	vec2 v1 = p2-p0;
	vec2 v2 = p1-p0;

	float area = abs(v1.x*v2.y - v1.y * v2.x);
	float distance0 = area/length(v0);
	float distance1 = area/length(v1);
	float distance2 = area/length(v2);

	//vertex[0]
	f_DistanceVector = vec3(distance0, 0.0, 0.0);
	f_Normal = g_Normal[0];
	f_Position = g_Position[0];
	gl_Position = gl_in[0].gl_Position;
	EmitVertex();

	//vertex[1]
	f_DistanceVector = vec3(0.0, distance1, 0.0);
	f_Normal = g_Normal[1];
	f_Position = g_Position[1];
	gl_Position = gl_in[1].gl_Position;
	EmitVertex();

	//vertex[2]
	f_DistanceVector = vec3(0.0, 0.0, distance2);
	f_Normal = g_Normal[2];
	f_Position = g_Position[2];
	gl_Position = gl_in[2].gl_Position;
	EmitVertex();

	EndPrimitive();
}