uniform sampler2D u_texture_color;
uniform sampler2D u_texture_emissive;
varying vec2 v_texCoords;

void main() {
	vec3 color = texture2D(u_texture_color, v_texCoords).xyz;
    color += texture2D(u_texture_emissive, v_texCoords).xyz; // todo rm 4
	gl_FragColor = vec4(color, 1);
}