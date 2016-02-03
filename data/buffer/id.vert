uniform mat4 projection;

attribute vec4 positionAttr;
attribute vec2 texCoordAttr;
attribute vec4 colorAttr;

varying vec2 texCoord;
varying vec4 color;

void main() {
   texCoord = texCoordAttr;
   color = colorAttr;
   gl_Position = projection * positionAttr;
}
