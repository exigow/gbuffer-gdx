varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec3 ambient = vec3(.70, .45, .17);
    gl_FragColor = texture2D(u_texture, v_texCoords) * vec4(ambient, 1);
}
