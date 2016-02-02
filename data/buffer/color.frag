varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_mask;
uniform float time;

void main() {
    float mtime = mod(time * .25, 1);
    float level = texture2D(u_texture_mask, v_texCoords).r;
    vec4 color = texture2D(u_texture, v_texCoords);
    if (level + mtime < 1)
        discard;
    if (level + mtime < 1.025)
        color = vec4(.75, .875, 1, color.a);
    gl_FragColor = color;
}
