uniform sampler2D u_texture;
varying vec2 v_texCoords;

vec4 diff(vec2 texel) {
    vec4 result = vec4( 0.0 );
    result -= texture2D( u_texture, v_texCoords.xy - texel);
    result += texture2D( u_texture, v_texCoords.xy + texel);
    return result;
}

void main() {
    vec4 horizEdge = diff(vec2(1.0 / 512, 0));
    vec4 vertEdge =  diff(vec2(0, 1.0 / 512));
    vec3 mid = texture2D( u_texture, v_texCoords.xy).rgb;
    vec3 edge = sqrt((horizEdge.rgb * horizEdge.rgb) + (vertEdge.rgb * vertEdge.rgb)) * mid;
    gl_FragColor = vec4(edge, 1);
}