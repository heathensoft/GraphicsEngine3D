#version 330

layout (location=0) in vec3 v_in_position;
layout (location=3) in vec4 v_in_color;
layout (location=1) in vec2 v_in_texCoords;

out vec4 v_out_color;
out vec2 v_out_texCoords;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{

    v_out_color = v_in_color;
    //v_out_color.a *= (255.0/254.0);
    v_out_texCoords = v_in_texCoords;
    gl_Position = projectionMatrix * modelViewMatrix * vec4(v_in_position, 1.0);
}
