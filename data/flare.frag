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

vec3 factors[3] = {
    vec3(1, 0, 0),
    vec3(0, 1, 0),
    vec3(0, 0, 1),
};

float z = .025;
float scales[3] = {
    1 + z, 1, 1 - z
};

void main() {
    vec2 dir = .5 - v_texCoords.xy;
    // bounces flare
    vec3 bouncesFlareColor = texture2D(u_texture, v_texCoords.xy).xyz;
    for (int f = 0; f < 3; f++) {
        for (int i = 0; i < 8; i++) {
            vec2 uv = v_texCoords.xy + dir * scales[f] * distances[i];
            bouncesFlareColor += texture2D(u_texture, uv).xyz * factors[f] * .075f;
        }
    }
    // circular flare
    vec2 normDir = normalize(dir) * 0.4f;
    vec3 curcilarFlareColor = vec3(0);
    for (int f = 0; f < 3; f++) {
        curcilarFlareColor += texture2D(u_texture, v_texCoords.xy + normDir * scales[f]).xyz * factors[f];
    }
    curcilarFlareColor *= .5 * pow(length(dir), 2);
    // mix
    gl_FragColor = vec4(bouncesFlareColor + curcilarFlareColor, 1);
}
