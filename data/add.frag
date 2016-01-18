uniform sampler2D u_texture_a;
uniform sampler2D u_texture_b;
varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture_a, v_texCoords).xyz;
    color += texture2D(u_texture_b, v_texCoords).xyz;
    color *= .5;
	gl_FragColor = vec4(color, 1);
}