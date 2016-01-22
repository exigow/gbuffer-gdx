attribute vec4 a_position;
attribute vec4 a_velocity;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec2 v_texCoords;
varying vec2 v_velocity;

void main() {
   v_texCoords = a_texCoord0;
   v_velocity = a_velocity.xy;
   gl_Position = u_projTrans * a_position;
}
