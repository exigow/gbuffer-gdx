varying vec2 v_texCoords;
uniform sampler2D u_texture;

float distances[8] = {
    0.5f,
    0.7f,
    1.03f,
    1.35,
    1.55f,
    1.62f,
    2.2f,
    3.9f
};

void main() {
    vec2 dir = .5 - v_texCoords.xy;
    vec3 result = texture2D(u_texture, v_texCoords.xy).xyz;
    for (int i = 0; i < 8; i++) {
        vec2 uv = v_texCoords.xy + dir * distances[i];
        result += texture2D(u_texture, uv).xyz * .075f;
    }
    gl_FragColor = vec4(result, 0);
}
