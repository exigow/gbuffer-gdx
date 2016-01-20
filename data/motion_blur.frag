uniform sampler2D u_texture_source;
uniform sampler2D u_texture_velocity;
varying vec2 v_texCoords;
uniform float texel;

const float weight[3] = {
	0.38774,
	0.24477,
	0.06136
};

void main() {
	vec2 velo = (.5 - texture2D(u_texture_velocity, v_texCoords).xy) * texel * 64;
	velo.x = -velo.x;
	vec3 color = vec3(0);
	color += texture2D(u_texture_source, v_texCoords - 2 * velo).xyz * weight[2];
	color += texture2D(u_texture_source, v_texCoords - 1 * velo).xyz * weight[1];
	color += texture2D(u_texture_source, v_texCoords).xyz * weight[0];
	color += texture2D(u_texture_source, v_texCoords + 1 * velo).xyz * weight[1];
	color += texture2D(u_texture_source, v_texCoords + 2 * velo).xyz * weight[2];
	gl_FragColor = vec4(color, 1);
}
