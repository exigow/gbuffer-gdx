varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec3 ambient = vec3(.17, .45, .90);
    vec4 color = texture2D(u_texture, v_texCoords) * vec4(ambient, 1);
    gl_FragColor = color;
}
