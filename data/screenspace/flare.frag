varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_lens_dirt;

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

vec3 distorted(vec2 uv, vec2 dir) {
    vec3 result = vec3(0);
    vec2 scale = dir * .025;
    result.r += texture2D(u_texture, uv + scale).r;
    result.g += texture2D(u_texture, uv).g;
    result.b += texture2D(u_texture, uv - scale).b;
    return result;
}

vec3 circularFlareColor(vec2 dir) {
    vec2 normDir = normalize(dir) * .4;
    vec3 curcilarFlareColor = distorted(v_texCoords + normDir, dir);
    curcilarFlareColor *= .875 * pow(length(dir), 2);
    return curcilarFlareColor;
}

vec3 bounce(int i, vec2 dir) {
    vec2 uv = v_texCoords + dir * distances[i];
    float weight = clamp(1 - length(vec2(.5) - uv) * 2, 0, 1);
    return distorted(uv, dir) * .125 * weight;
}

void main() {
    vec2 dir = .5 - v_texCoords.xy;
    vec3 bounces = distorted(v_texCoords, dir);
    for (int i = 0; i < 8; i++)
        bounces += bounce(i, dir);
    vec3 circular = circularFlareColor(dir);
    vec3 dirt = texture2D(u_texture_lens_dirt, v_texCoords).xyz;
    vec3 combined = bounces + circular;
    gl_FragColor = vec4(combined + (combined * dirt), 1);
}
