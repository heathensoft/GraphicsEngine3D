#version 420

layout (location=0) in vec3 v_in_position;
layout (location=3) in vec4 v_in_color;
layout (location=1) in vec2 v_in_texCoords;


out VS_OUT {
    vec2 texCoords;
    vec4 color;
} vs_out;

//out vec4 v_out_color;
//out vec2 v_out_texCoords;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{

    vs_out.color = v_in_color;
    //v_out_color.a *= (255.0/254.0);
    vs_out.texCoords = v_in_texCoords;
    gl_Position = projectionMatrix * modelViewMatrix * vec4(v_in_position, 1.0);
}
