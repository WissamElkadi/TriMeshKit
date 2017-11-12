attribute vec3 v_Position;
attribute vec3 v_Normal;

uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;

varying vec3 f_Normal;

void main()
{
    f_Normal = normalize(mat3(u_ModelViewMatrix) * v_Normal);

    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(v_Position, 1.0);
}