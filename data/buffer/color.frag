varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_mask;
uniform float time;

void main() {
    float level = texture2D(u_texture_mask, v_texCoords).r;
    vec3 ambient = vec3(.17, .45, .90);
    vec4 color = texture2D(u_texture, v_texCoords) * vec4(ambient, 1);
    if (level + time < 1)
        discard;
    if (level + time < 1.025)
        color = vec4(.75, .875, 1, color.a);
    gl_FragColor = color;
}
