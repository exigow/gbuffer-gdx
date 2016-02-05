uniform sampler2D texture;

varying vec4 color;

void main() {
    vec4 raw = texture2D(texture, gl_PointCoord);
    gl_FragColor = raw * color;
}