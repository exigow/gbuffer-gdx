attribute vec4 a_position;
attribute vec4 a_velocity;
uniform mat4 u_projTrans;
varying vec2 v_velocity;

void main() {
   v_velocity = a_velocity.xy;
   gl_Position = u_projTrans * a_position;
}
