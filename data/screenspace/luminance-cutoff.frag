uniform sampler2D u_texture;
varying vec2 v_texCoords;

float luminance(vec3 source) {
	return dot(source, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
	vec3 color = texture2D(u_texture, v_texCoords).xyz;
	float luma = luminance(color);
	float powered = pow(luma, 4);
	gl_FragColor = vec4(color * powered, 1);
}