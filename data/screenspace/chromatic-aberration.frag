uniform sampler2D u_texture;
varying vec2 v_texCoords;

// based on http://www.francois-tarlier.com/blog/cubic-lens-distortion-shader/
vec2 uvDistortion(float k, float kcube) {
    float r2 = (v_texCoords.x - .5) * (v_texCoords.x - .5) + (v_texCoords.y - .5) * (v_texCoords.y - .5);
    float f = 0;
    f = 1 + r2 * (k + kcube * sqrt(r2));
    float x = f * (v_texCoords.x - .5) + .5;
    float y = f * (v_texCoords.y - .5) + .5;
    return vec2(x, y);
}

const vec3 spectrumSample[5] = {
    vec3(.5, 0, 0),
    vec3(.5, .3333, 0),
    vec3(0, .3333, 0),
    vec3(0, .3333, .5),
    vec3(0, 0, .5)
};

void main() {
    vec3 color = vec3(0);
    for (int i = 0; i < 5; i++) {
        float n = i / 5.0;
        vec2 coord = uvDistortion(-.075 * n, n * .025);
        color += texture2D(u_texture, coord).rgb * spectrumSample[i];
    }
    gl_FragColor = vec4(color, 1);
}
