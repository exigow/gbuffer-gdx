uniform sampler2D texture;

varying vec2 texCoord;

void main() {
    vec3 ambient = vec3(.17, .45, .90);
    vec4 color = texture2D(texture, texCoord) * vec4(ambient, 1);
    gl_FragColor = color;
}
