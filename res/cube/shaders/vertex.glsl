#version 330

layout (location=0) in vec3 v_in_position;
layout (location=1) in vec3 v_in_color;

out vec3 v_out_color;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * vec4(v_in_position, 1.0);
    v_out_color = v_in_color;
}
