uniform sampler2D u_texture;
varying vec2 v_texCoords;

void main() {
	vec3 color = texture2D(u_texture, v_texCoords).xyz;
	float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722));
	float powered = pow(brightness, 4);
	gl_FragColor = vec4(color * powered, 1);
}