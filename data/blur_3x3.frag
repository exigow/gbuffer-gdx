uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float texel;
uniform float vecX;
uniform float vecY;

const float offset[3] = {0.0, 1.3846153846, 3.2307692308};
const float weight[3] = {0.2270270270, 0.3162162162, 0.0702702703};

vec3 lookup(int i) {
	int ai = abs(i);
	float off = offset[ai];
	return texture2D(u_texture, v_texCoords + vec2(vecX * i * off, vecY * i * off) * texel).xyz * weight[ai];
}

void main() {
	vec3 color = texture2D(u_texture, v_texCoords) * weight[0];
	for (int i = 1; i < 3; i++) {
		color += lookup(i) + lookup(-i);
	}
	gl_FragColor = vec4(color, 1);
}
