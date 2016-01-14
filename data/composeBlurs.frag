uniform sampler2D u_texture[4];
varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture[0], v_texCoords).xyz;
    color += texture2D(u_texture[1], v_texCoords).xyz;
    color += texture2D(u_texture[2], v_texCoords).xyz;
    color += texture2D(u_texture[3], v_texCoords).xyz;
    //color *= .25f;
	gl_FragColor = vec4(color, 1);
}