uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float texel;

const float weight[5] = {
	0.2270270270,
	0.1945945946,
	0.1216216216,
	0.0540540541,
	0.0162162162
};

vec3 lookup(float x, float y, float weigth) {
	return texture2D(u_texture, v_texCoords + vec2(texel * x, texel * y) * 1.33).xyz * weigth;
}

void main() {
	vec3 color = lookup(0, 0, .25);
	color += lookup(1, 1, .0625);
	color += lookup(1, -1, .0625);
	color += lookup(-1, 1, .0625);
	color += lookup(-1, -1, .0625);
	color += lookup(1, 0, .125);
	color += lookup(-1, 0, .125);
	color += lookup(0, 1, .125);
	color += lookup(0, -1, .125);
	gl_FragColor = vec4(color, 1);
}