uniform sampler2D texture;

varying vec2 texCoord;
varying vec4 color;

void main() {
    float alpha = texture2D(texture, texCoord).a;
    vec4 colored = vec4(1, 1, 1, alpha) * color;
    gl_FragColor = colored;
}
