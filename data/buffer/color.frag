varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    //vec3 ambient = vec3(.90, .45, .17); // red
    vec3 ambient = vec3(.17, .45, .90);
    gl_FragColor = texture2D(u_texture, v_texCoords) * vec4(ambient, 1);
}
