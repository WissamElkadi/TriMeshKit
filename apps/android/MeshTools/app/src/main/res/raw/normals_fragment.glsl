precision highp float;

////Input
varying vec3 f_Normal;

void main()
{
	gl_FragColor.xyz = f_Normal;
    gl_FragColor.w = 1.0;
}