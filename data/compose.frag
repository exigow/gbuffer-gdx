uniform sampler2D u_texture_color;
uniform sampler2D u_texture_bloom;
varying vec2 v_texCoords;

vec3 factors[3] = {
    vec3(1, 0, 0),
    vec3(0, 1, 0),
    vec3(0, 0, 1),
};

float z = .05;
float scales[3] = {
    1 + z, 1, 1 - z
};

void main() {
	vec3 color = texture2D(u_texture_color, v_texCoords).xyz;
	color += texture2D(u_texture_bloom, v_texCoords).xyz;
	gl_FragColor = vec4(color, 1);
}