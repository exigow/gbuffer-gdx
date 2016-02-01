varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_destruction_mask;
uniform sampler2D u_texture_fire_pattern;
uniform sampler2D u_texture_fire_pattern_mask;
uniform float time;

vec4 calcFireColor() {
    vec4 fire = texture2D(u_texture_fire_pattern, v_texCoords + vec2(.131, .174) * time);
    float mask = texture2D(u_texture_fire_pattern_mask, v_texCoords + vec2(.164, .117) * time).r;
    return fire * mask;
}

float calcEffectStrength(float value, float minVal, float maxVal) {
    float range = (value - minVal) / (maxVal - minVal);
    float absolute = abs(range);
    float caged = clamp(absolute, 0, 1);
    return 1 - caged;
}

void main() {
    vec3 ambient = vec3(.17, .45, .90);
    vec4 color = texture2D(u_texture, v_texCoords) * vec4(ambient, 1);
    float level = texture2D(u_texture_destruction_mask, v_texCoords * 2).r;
    vec4 fireColor = calcFireColor();
    float fireStrength = calcEffectStrength(level, .8, .85);
    color += (fireStrength * fireColor) * color.a;
    if (level > .825)
        color *= vec4(vec3(.5), 1);
    gl_FragColor = color;
}
