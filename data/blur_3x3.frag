uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform float texel;

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