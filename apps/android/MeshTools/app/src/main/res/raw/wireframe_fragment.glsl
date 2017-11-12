#version 310 es

precision highp float;

////Input
in vec3 f_Normal;
in vec3 f_Position;
in vec3 f_DistanceVector;

////Uniform
uniform mat4 u_ModelViewMatrix;
uniform vec3 u_CameraPosition;
uniform vec3 u_CameraDirection;

//out
out vec4 fColor;

void main()
{
	// Normalize normal in eye space
	vec3 normal = normalize(f_Normal);

    // Camera position in model view space
    vec3 cameraPosition =  vec3(u_ModelViewMatrix * vec4(u_CameraPosition, 1.0));

	//[1] Compute the ambient term
	vec3 ambientColor = vec3(0.2, 0.2, 0.2);

	// [2]Compute the diffuse term
	vec3 lightDirection = - normalize(u_CameraDirection);
	float diffuseLight = max(dot(normal, lightDirection), 0.0);
    vec3 diffuseColor = diffuseLight * vec3(1.0, 0.0, 0.0);

	// [3]Compute the specular term
	vec3 viewDirection = normalize(cameraPosition - f_Position);
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float specularLight = pow(max(dot(viewDirection, reflectDirection), 0.0), 50.2);
    vec3 specularColor = specularLight * vec3(1.0, 0.0, 0.0);

	// [4]Define the final vertex color
	vec3 fillColor =  ambientColor + diffuseColor + specularColor;

	//// Compute the shortest distance to the edge
	float minDistance = min(f_DistanceVector[0], min(f_DistanceVector[1], f_DistanceVector[2]));

	//// Compute line intensity
	////exponential curve when d is large I approach to zero , and when d approach to zero , i approach to +1
	float wireIntensity = exp2(-1.0*minDistance*minDistance);

	vec3 wireColor = vec3(0.2, 0.2, 0.2);

    if(any(lessThan(f_DistanceVector, vec3(0.99)))){
        fColor = vec4(fillColor, 1.0);
    }
    else{
        discard;
    }
}