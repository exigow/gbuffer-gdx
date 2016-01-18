uniform sampler2D u_texture_a;
uniform sampler2D u_texture_b;
uniform sampler2D u_texture_c;
uniform sampler2D u_texture_d;

varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture_a, v_texCoords).xyz;
    color += texture2D(u_texture_b, v_texCoords).xyz;
    color += texture2D(u_texture_c, v_texCoords).xyz;
    color += texture2D(u_texture_d, v_texCoords).xyz;
    color *= .25;
	gl_FragColor = vec4(color, 1);
}