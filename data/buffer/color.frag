varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_destruction_mask;
uniform sampler2D u_texture_fire_pattern;
uniform sampler2D u_texture_fire_pattern_mask;
uniform sampler2D u_texture_hull;
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
    float level = texture2D(u_texture_destruction_mask, v_texCoords).r;
    vec4 fireColor = calcFireColor();
    float smokeStrength = calcEffectStrength(level, .375, .65);
    float fireStrength = calcEffectStrength(level, .6, .75);
    float hullStrength = clamp(calcEffectStrength(level, .75, 1) * 16, 0, 1);

    vec4 fire = fireColor * fireStrength * color.a;
    vec4 smoke = vec4(vec3(1 - smokeStrength), 1);

    vec4 hullTex = texture2D(u_texture_hull, v_texCoords * 4);
    vec4 hullOrColor = vec4(mix(color.rgb, hullTex.rgb, hullStrength), color.a) * smoke;

    gl_FragColor = hullOrColor + fire;
}
