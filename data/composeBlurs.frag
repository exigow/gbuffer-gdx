uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;
varying vec2 v_texCoords;

void main() {
	vec3 color = vec3(0);
    color += texture2D(u_texture0, v_texCoords).xyz;
    color += texture2D(u_texture1, v_texCoords).xyz;
    color += texture2D(u_texture2, v_texCoords).xyz;
    color += texture2D(u_texture3, v_texCoords).xyz;
    //color *= .25f;
	gl_FragColor = vec4(color, 1);
}