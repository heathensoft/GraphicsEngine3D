#version 420

layout (location=0) in vec4 loc_pos;
layout (location=1) in vec3 loc_nor;

out VS_OUT {
    vec3 pos;
    vec3 nor;
} _out;

uniform mat4 u_modelView;
uniform mat4 u_projection;

void main() {

    vec4 mvPos = u_modelView * loc_pos;
    gl_Position = u_projection * mvPos;
    _out.nor = normalize(u_modelView * vec4(loc_nor, 0.0)).xyz;
    _out.pos = mvPos.xyz;

}
