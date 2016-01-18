uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float texel;
uniform float vecX;
uniform float vecY;

const float weight[8] = {
	0.159576912161,
	0.147308056121,
	0.115876621105,
	0.077674421993,
	0.044368333871,
	0.021596386605,
	0.008957812117,
	0.004429912105
};

vec3 lookup(int i) {
	int ai = abs(i);
	return texture2D(u_texture, v_texCoords + vec2(vecX, vecY) * i * texel).xyz * weight[ai];
}

void main() {
	vec3 color = texture2D(u_texture, v_texCoords) * weight[0];
	for (int i = 1; i < 8; i++)
		color += lookup(i) + lookup(-i);
	gl_FragColor = vec4(color, 1);
}

