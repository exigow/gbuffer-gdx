uniform sampler2D u_texture;
varying vec2 v_texCoords;

const vec2 texel = vec2(1.0 / 1280.0, 1.0 / 960.0);

void main() {
    vec4 result = vec4(0);
    vec2 dir = (0.5 - v_texCoords) * texel;
    dir *= 3; // make it more visible; just tweaks
    result.r += texture2D(u_texture, v_texCoords + dir).r;
    result.g += texture2D(u_texture, v_texCoords).g;
    result.b += texture2D(u_texture, v_texCoords - dir).b;
    gl_FragColor = result;
}