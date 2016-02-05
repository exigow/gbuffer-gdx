uniform mat4 projection;

attribute vec3 positionAttr;
attribute vec4 colorAttr;

varying vec4 color;

void main() {
    color = colorAttr;
    vec4 position = projection * vec4(positionAttr.xyz, 1);
    gl_Position = position;
    vec3 ndc = gl_Position.xyz / gl_Position.w;
    float zDist = 1.0 - ndc.z;
    gl_PointSize = 512 * zDist;
}