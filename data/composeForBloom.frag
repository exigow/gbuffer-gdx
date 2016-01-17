uniform sampler2D u_texture_color;
uniform sampler2D u_texture_emissive;
varying vec2 v_texCoords;

void main() {
	vec3 color = texture2D(u_texture_color, v_texCoords).xyz;
    float lum = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
    color *= pow(lum, 12);
    color += texture2D(u_texture_emissive, v_texCoords).xyz;
	gl_FragColor = vec4(color, 1);
}