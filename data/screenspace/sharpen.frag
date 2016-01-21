uniform sampler2D u_texture;
varying vec2 v_texCoords;

const vec2 texel = vec2(1.0 / 1280, 1.0 / 960);

const float kernel[9] = {
    0, -1, 0,
    -1, 5, -1,
    0, -1, 0
};

vec2 offset[9] = {
    vec2(-texel.x, -texel.y),
    vec2(0, -texel.y),
    vec2(texel.x, -texel.y),
    vec2(-texel.x, 0),
    vec2(0, 0),
    vec2(texel.x, 0),
    vec2(-texel.x, texel.y),
    vec2(0, texel.y),
    vec2(texel.x, texel.y)
};


void main() {
    vec4 result = vec4(0);
    for (int i = 0; i < 9; i++)
        result += texture2D(u_texture, v_texCoords + offset[i]) * kernel[i];
    gl_FragColor = result;
}