#version 430

layout (location=0) in vec4 a_pos;

uniform mat4 u_shadowMV;
uniform mat4 u_shadowP;

void main() {

    gl_Position = u_shadowP * u_shadowMV * a_pos;
}
