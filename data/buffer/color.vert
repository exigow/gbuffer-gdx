uniform mat4 projection;

attribute vec4 a_position;
attribute vec2 a_texCoord0;

varying vec2 texCoord;

void main() {
   texCoord = a_texCoord0;
   gl_Position = projection * a_position;
}
