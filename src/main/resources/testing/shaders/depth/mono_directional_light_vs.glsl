#version 430

// we will need texCoords here later becauce we do not want shadow
// when the mesh / sprite had translucent pixels
// I have seen Thin Matrix implement this in some video

// i can use the same shaders for sprite(batches) and meshes
// even if sprites is batched and not instanced.
// as long as the layouts used are the same

layout (location=0) in vec4 a_pos;
layout (location=X) in mat4 a_model;

uniform mat4 u_lightSpace;

void main() {

    gl_Position = u_lightSpace * a_model * a_pos;
}
