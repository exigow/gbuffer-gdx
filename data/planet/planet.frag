varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_rotation;

void main() {
    vec2 p = -1 + 2 * v_texCoords.xy;
    float r = sqrt(dot(p, p));
    if (r > 1) discard;
    float f = (1 - sqrt(1 - r)) / r;
    vec2 uv = p * f * vec2(.25, .5) + vec2(u_rotation, .5);
    float fersnel = clamp(pow(f, 4), 0, 1);
    vec4 atmosphere = vec4(1, 1, 1, 1) * fersnel;
    gl_FragColor = texture2D(u_texture, uv) + atmosphere;
}
