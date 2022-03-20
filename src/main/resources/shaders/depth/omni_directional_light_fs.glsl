#version 430 core

in vec4 fragPos;

uniform vec3 u_lightPos;
uniform float u_farPlane;

void main()
{
    // get distance between fragment and light source
    float lightDistance = length(fragPos.xyz - u_lightPos);
    // map to [0-1] range by dividing by farPlane
    // we can do this becausem the frustum-box's near plane is 0.0f
    // meaning: farPlane = farplane - nearPlane
    lightDistance = lightDistance / u_farPlane;
    // write this as modified depth
    gl_FragDepth = lightDistance;
}
