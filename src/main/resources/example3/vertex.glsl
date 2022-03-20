#version 430


layout (location=0) in vec4 aPosition;
layout (location=1) in vec4 aColor;
layout (location=2) in float aBitmask;

out vec4 fColor;

uniform mat4 uProjectionView;

vec4 f = aPosition;

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

    int g = int(aBitmask);
    fColor = vec4(1,1,1,1);
    if((g & 6) == 6) {
        fColor = aColor;
        fColor.a *= (255.0/254.0);
    }
    gl_Position = uProjectionView * f;

}
