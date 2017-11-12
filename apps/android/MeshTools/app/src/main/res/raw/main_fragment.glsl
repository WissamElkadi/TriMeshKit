precision highp float;

////Input
varying vec3 f_Normal;
varying vec3 f_Position;

////Uniform
uniform mat4 u_ModelViewMatrix;
uniform vec3 u_CameraPosition;
uniform vec3 u_CameraDirection;

void main()
{
	// Normalize normal in eye space
	vec3 normal = normalize(f_Normal);

    // Camera position in model view space
    vec3 cameraPosition =  vec3(u_ModelViewMatrix * vec4(u_CameraPosition, 1.0));

	//[1] Compute the ambient term
	vec3 ambientColor = vec3(0.2, 0.2, 0.2);

	//[2] Compute the diffuse term
	vec3 lightDirection = - normalize(u_CameraDirection);
	float diffuseLight = max(dot(normal, lightDirection), 0.0);
	vec3 diffuseColor = diffuseLight * vec3(1.0, 0.0, 0.0);

	//[3] Compute the specular term
	vec3 viewDirection = normalize(cameraPosition - f_Position);
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float specularLight = pow(max(dot(viewDirection, reflectDirection), 0.0), 50.2);
	vec3 specularColor = specularLight * vec3(1.0, 0.0, 0.0);

	//[4] Define the final vertex color
	vec3 fillColor =  ambientColor + diffuseColor + specularColor;

	gl_FragColor.xyz = fillColor;
    gl_FragColor.w = 1.0;
}