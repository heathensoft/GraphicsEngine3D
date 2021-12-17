#version 400

layout (location=0) in vec4 loc_pos;
layout (location=1) in vec3 loc_nor;

out VS_OUT {
    vec3 pos;
    vec3 nor;
} _out;

uniform mat4 modelView;
uniform mat4 projection;

void main() {

    vec4 mvPos = modelView * loc_pos;
    gl_Position = projection * mvPos;
    _out.nor = normalize(modelView * vec4(loc_nor, 0.0)).xyz;
    _out.pos = mvPos.xyz;

}
