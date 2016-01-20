uniform sampler2D u_texture_color;
uniform sampler2D u_texture_emissive;
uniform sampler2D u_texture_velocity;
varying vec2 v_texCoords;
uniform float texel;

const float weight[3] = {
	0.38774,
	0.24477,
	0.06136
};

vec2 unpackVelocity() {
    vec2 velo = (.5 - texture2D(u_texture_velocity, v_texCoords).xy) * texel * 64;
    velo.x = -velo.x;
    return velo;
}

vec3 blurSample(sampler2D source, vec2 velo) {
    vec3 color = vec3(0);
    color += texture2D(source, v_texCoords - 2 * velo).xyz * weight[2];
    color += texture2D(source, v_texCoords - 1 * velo).xyz * weight[1];
    color += texture2D(source, v_texCoords).xyz * weight[0];
    color += texture2D(source, v_texCoords + 1 * velo).xyz * weight[1];
    color += texture2D(source, v_texCoords + 2 * velo).xyz * weight[2];
    return color;
}

void main() {
    vec2 velocity = unpackVelocity();
	vec3 color = blurSample(u_texture_color, velocity);
    vec3 emissive = blurSample(u_texture_emissive, velocity) * 4;
	gl_FragColor = vec4(color + emissive, 1);
}
