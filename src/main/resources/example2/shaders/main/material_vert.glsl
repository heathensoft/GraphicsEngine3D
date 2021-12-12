#version 330

layout (location=0) in vec4 loc_pos;
layout (location=1) in vec3 loc_nor;

out vec3 out_pos;
out vec3 out_nor;


uniform mat4 modelView;
uniform mat4 projection;

void main() {

    vec4 mvPos = modelView * loc_pos;
    gl_Position = projection * mvPos;
    out_nor = normalize(modelView * vec4(loc_nor, 0.0)).xyz;
    out_pos = mvPos.xyz;

}
