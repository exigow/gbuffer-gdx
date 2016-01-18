uniform sampler2D u_texture;
varying vec2 v_texCoords;

void main() {
	vec3 color = texture2D(u_texture, v_texCoords).xyz;
    float lum = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
    color *= pow(lum, 2);
	gl_FragColor = vec4(color, 1);
}