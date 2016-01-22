uniform sampler2D u_texture_base;
uniform sampler2D u_texture_bloom;
varying vec2 v_texCoords;

void main() {
	vec3 color = texture2D(u_texture_base, v_texCoords).xyz;
	color += texture2D(u_texture_bloom, v_texCoords).xyz;
	gl_FragColor = vec4(color, 1);
}