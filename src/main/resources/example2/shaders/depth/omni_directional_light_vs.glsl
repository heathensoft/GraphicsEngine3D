#version 430

layout (location=0) in vec4 a_pos;

uniform mat4 u_model; // this will be a layout

void main() {

    gl_Position = u_model * a_pos;
}
