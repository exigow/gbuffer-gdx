uniform sampler2D u_texture;
varying vec2 v_texCoords;

void main()
{
    float x = 1.0 / 512;
    float y = 1.0 / 512;
    vec4 horizEdge = vec4( 0.0 );
    horizEdge -= texture2D( u_texture, vec2(v_texCoords.x - x, v_texCoords.y     ));
    horizEdge += texture2D( u_texture, vec2(v_texCoords.x + x, v_texCoords.y     ));
    vec4 vertEdge = vec4( 0.0 );
    vertEdge -= texture2D( u_texture, vec2( v_texCoords.x    , v_texCoords.y - y ));
    vertEdge += texture2D( u_texture, vec2( v_texCoords.x    , v_texCoords.y + y ));
    vec3 edge = sqrt((horizEdge.rgb * horizEdge.rgb) + (vertEdge.rgb * vertEdge.rgb));

    gl_FragColor = vec4( edge, texture2D( u_texture, v_texCoords ).a );
}