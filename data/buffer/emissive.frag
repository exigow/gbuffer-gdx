varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_mask;
uniform float time;

void main() {
    float level = texture2D(u_texture_mask, v_texCoords).r;
    vec4 color = texture2D(u_texture, v_texCoords);
    if (level + time < 1)
        discard;
    if (level + time < 1.075)
        color = vec4(.15, .25, 1, color.a);
    gl_FragColor = color;
}
