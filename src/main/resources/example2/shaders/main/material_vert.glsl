#version 430

layout (location=0) in vec4 a_pos;
layout (location=1) in vec3 a_nor;

out VS_OUT {
    vec3 pos;
    vec3 nor;
    vec4 lpos;
} _out;

uniform mat4 u_modelView;
uniform mat4 u_projection;
uniform mat4 u_light_mvp;

void main() {

    vec4 mvPos = u_modelView * a_pos;
    gl_Position = u_projection * mvPos;
    _out.nor = normalize(u_modelView * vec4(a_nor, 0.0)).xyz;
    _out.pos = mvPos.xyz;
    _out.lpos = u_light_mvp * a_pos;
}
