uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float scale;

void main() {
	vec3 result = vec3(0);

	result += texture2D(u_texture, v_texCoords + vec2(-scale, scale)).xyz;
	result += texture2D(u_texture, v_texCoords + vec2(scale, scale)).xyz;
	result += texture2D(u_texture, v_texCoords + vec2(scale, -scale)).xyz;
	result += texture2D(u_texture, v_texCoords + vec2(-scale, -scale)).xyz;

	result *= .25f;

	gl_FragColor = vec4(result, 1);
}