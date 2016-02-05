uniform sampler2D texture;

void main() {
    vec4 color = texture2D(texture, gl_PointCoord);
    gl_FragColor = color;
}