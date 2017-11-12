#version 120

//Uniform
uniform vec3 ambient_color;
uniform vec3 diffuse_color;
uniform vec3 specular_color;    
uniform vec3 emissive_color;
uniform float shininess;
uniform vec3 camera_position;
uniform vec3 camera_direction;

varying vec3 ge_normal;
varying vec3 ge_position; 

void main()
{
	const vec3 lightColor    = vec3(1, 1, 1);
	const vec3 globalAmbient = vec3(0.2, 0.2, 0.2);
	// Normalize normal in eye space
	vec3 norm = normalize(ge_normal);

	//[1] Compute the ambient term
	vec3 ambient = globalAmbient * ambient_color;

	// [2]Compute the diffuse term
	vec3 light_dir = - camera_direction;
	float diffuseLight = max(dot(norm, light_dir), 0.0);
	vec3 diffuse = diffuseLight * lightColor * diffuse_color;

	// [3]Compute the specular term
	vec3 viewDirection = normalize(camera_position - ge_position);
	vec3 reflectDirection = reflect(-light_dir, norm); 
	float specularLight = pow(max(dot(viewDirection, reflectDirection), 0.0), shininess);
	vec3 specular = specularLight * lightColor * specular_color;  

	// [4]Define the final vertex color
	gl_FragColor.xyz = ambient + diffuse + specular + emissive_color;
	gl_FragColor.w = 1.0;
} 

