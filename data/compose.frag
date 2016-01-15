uniform sampler2D u_texture_color;
uniform sampler2D u_texture_emissive;

varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture_color, v_texCoords).xyz;
    color += texture2D(u_texture_emissive, v_texCoords).xyz;
	gl_FragColor = vec4(color, 1);
}