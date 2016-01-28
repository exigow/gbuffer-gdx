varying vec2 v_texCoords;
varying vec4 v_color;
uniform sampler2D u_texture;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a;
    vec4 colored = vec4(1, 1, 1, alpha) * v_color;
    gl_FragColor = colored;
}
