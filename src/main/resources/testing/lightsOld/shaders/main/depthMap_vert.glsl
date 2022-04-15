#version 430

layout (location=0) in vec4 a_pos;

uniform mat4 u_light_mvp;

void main() {

    gl_Position = u_light_mvp * a_pos;
}
