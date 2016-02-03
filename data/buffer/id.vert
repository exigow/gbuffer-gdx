uniform mat4 projection;

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

varying vec2 texCoord;
varying vec4 color;

void main() {
   texCoord = a_texCoord0;
   color = a_color;
   gl_Position = projection * a_position;
}
