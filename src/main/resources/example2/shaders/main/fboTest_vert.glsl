#version 430

layout (location = 0) in vec2 a_pos;
layout (location = 1) in vec2 a_tex;

out VS_OUT {
    vec2 texCoords;
} _out;

void main()
{
    gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0);
    _out.texCoords = a_tex;
}
