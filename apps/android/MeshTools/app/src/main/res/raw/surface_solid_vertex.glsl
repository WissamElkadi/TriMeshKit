//Uniform
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform mat4 normal_matrix;

//Output
varying vec3 ge_normal;
varying vec3 ge_position;

void main()
{
	gl_Position = projection_matrix * view_matrix * model_matrix * gl_Vertex;
	ge_normal = vec3(normal_matrix * vec4(gl_Normal, 0.0));
	ge_position = vec3(model_matrix * gl_Vertex);
}