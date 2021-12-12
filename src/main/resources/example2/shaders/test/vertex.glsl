#version 330

layout (location=0) in vec3 v_in_position;
layout (location=1) in vec3 v_in_normal;

out vec3 mvVertexPos;
out vec3 mvVertexNormal;


uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
//uniform mat3 normalMatrix;

void main()
{


}
