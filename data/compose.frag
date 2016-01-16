uniform sampler2D u_texture1;
uniform sampler2D u_texture0;

varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture0, v_texCoords).xyz * .1f;
    color += texture2D(u_texture1, v_texCoords).xyz;
	gl_FragColor = vec4(color, 1);
}