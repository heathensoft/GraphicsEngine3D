#version 430

layout (location = 0) in vec4 aPos;
layout (location = 1) in vec2 aTexCoord;


out vec2 texCoords;

void main()
{
    gl_Position = aPos;
    texCoords = aTexCoord;
}
