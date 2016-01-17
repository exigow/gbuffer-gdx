uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float texel;
uniform float vecX;
uniform float vecY;

const float weight[5] = {
	0.2270270270,
	0.1945945946,
	0.1216216216,
	0.0540540541,
	0.0162162162
};

vec3 lookup(int step) {
	return texture2D(u_texture, v_texCoords + vec2(vecX * step, vecY * step) * texel).xyz * weight[abs(step)];
}

void main() {
	vec3 color = texture2D(u_texture, v_texCoords) * weight[0];
	for (int i = 1; i < 5; i++) {
		color += lookup(i) + lookup(-i);
	}
	gl_FragColor = vec4(color, 1);
}
