#version 430

// per vertex data
layout (location=0) in vec3 aFace_top;
layout (location=1) in vec3 aFace_left;
layout (location=2) in vec3 aFace_right;
layout (location=3) in vec3 aFace_bottom;
layout (location=4) in vec3 aFace_front;
layout (location=5) in vec3 aFace_rear;

// Per instance data (Quad)
layout (location=6) in vec3 aPosition; // 3 * float
layout (location=7) in vec4 aColor;    // 4 * unsigned byte
layout (location=8) in float aFlags;   // 1 * float

out vec4 vColor;
out vec4 vNormalVS;   // View Space
out vec3 vPositionVS; // View Space
//out vec4 vLightPosition;

uniform mat4 uView;
//uniform mat4 uProjection;
uniform mat4 uProjectionView;

// using this instead of if-statements
vec3 face_offsets[6] =
{
    aFace_top,
    aFace_left,
    aFace_right,
    aFace_bottom,
    aFace_front,
    aFace_rear,
};

const vec3 face_normals[6] =
{
    vec3(0.0,1.0,0.0),  // TOP
    vec3(-1.0,0.0,0.0), // LEFT
    vec3(1.0,0.0,0.0),  // RIGHT
    vec3(0.0,-1.0,0.0), // BOTTOM
    vec3(0.0,0.0,1.0),  // FRONT
    vec3(0.0,1.0,-1.0), // REAR
};

void main() {

    int face_index = int(aFlags) & 7;

    // Color (readjusting the alpha)
    vColor = aColor;
    vColor.a *= (255.0/254.0);

    // Normal (View Space)
    vec3 face_normal = face_normals[face_index];
    vNormalVS = normalize(uView * vec4(face_normal, 0.0));

    // Position (View Space)
    vec4 face_vertex_pos = vec4((face_offsets[face_index] + aPosition),1.0);
    gl_Position = uProjectionView * face_vertex_pos;
    vPositionVS = (uView * face_vertex_pos).xyz;
}
