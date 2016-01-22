varying vec2 v_texCoords;
varying vec2 v_velocity;
uniform sampler2D u_texture;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(v_velocity, 0, color.a);
}
