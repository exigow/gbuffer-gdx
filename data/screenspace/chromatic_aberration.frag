uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform vec2 texel;
//const vec2 texel = vec2(1.0 / 1280.0, 1.0 / 960.0);

const vec3 samples[5] = {
    vec3(.5, 0, 0),
    vec3(.5, .33, 0),
    vec3(0, .33, 0),
    vec3(0, .33, .5),
    vec3(0, 0, .5)
};

void main() {
    vec3 result = vec3(0);
    vec2 dir = (.5 - v_texCoords) * texel;
    dir *= 8; // make it more visible; just tweaks
    result += texture2D(u_texture, v_texCoords + dir).xyz * samples[0];
    result += texture2D(u_texture, v_texCoords + dir * .5).xyz * samples[1];
    result += texture2D(u_texture, v_texCoords).xyz * samples[2];
    result += texture2D(u_texture, v_texCoords - dir * .5).xyz * samples[3];
    result += texture2D(u_texture, v_texCoords - dir).xyz * samples[4];
    gl_FragColor = vec4(result, 1);
}
