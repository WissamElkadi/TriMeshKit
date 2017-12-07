
attribute vec2 v_Position;

void main()
{
    gl_Position = vec4(v_Position, -1.0,  1.0);
}