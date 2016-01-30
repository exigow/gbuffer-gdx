varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture_atmo;
uniform float u_rotation;

float luminance(vec3 source) {
	return dot(source, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    vec2 p = -1 + 2 * v_texCoords.xy;
    vec2 light = p + vec2(.25, .25);
    float r = sqrt(dot(p, p));
    float rLight = sqrt(dot(light, light));
    if (r > 1) discard;
    float f = (1 - sqrt(1 - r)) / r;
    vec2 uv = p * f * vec2(.5, .5);
    float fersnel = clamp(pow(f, 4), 0, 1);
    vec4 atmosphere = vec4(1, 1, 1, 1) * fersnel;
    float ld = (1 - rLight) * .75 + .25;
    vec4 color = texture2D(u_texture, uv + vec2(u_rotation, .5));
    vec4 colorAtmo = texture2D(u_texture_atmo, uv + vec2(u_rotation * 2, .5));
    vec4 colorMix = mix(color, vec4(colorAtmo.rgb, 1), colorAtmo.a);
    float shiness = pow(luminance(colorMix) * ld * 1.25, 8);
    gl_FragColor = colorMix * vec4(vec3(ld), 1) + atmosphere * ld + shiness;
}
