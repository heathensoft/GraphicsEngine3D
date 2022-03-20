#version 430


in vec4 vColor;
in vec4 vNormalVS;   // View Space
in vec3 vPositionVS; // View Space

out vec4 fColor;

uniform vec3 uLightDirection;

const float shine = 20.0;
const float ambience = 0.2;
const float diffuse = 0.5;
const vec3 specular = vec3(0.7,0.7,0.7);
const vec3 lightColor = vec3(0.8,0.8,0.8);

float energyConservation(float shine) {
    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );
}

void main() {
    // Just directional light
    vec3 toEyeDir = normalize(-vPositionVS);
    float ec = energyConservation(shine);
    vec3 toLightDir = -normalize(uLightDirection);
    float diff = max(dot(vNormalVS.xyz, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEyeDir);
    float spec = pow(max(dot(vNormalVS.xyz,halfwayDir),0.0),shine) * ec;
    vec3 A = lightColor * vColor.rgb * ambience;
    vec3 D = lightColor * vColor.rgb * diffuse * diff;
    vec3 S = lightColor * specular * spec;
    vec3 color = A + D + S;
    fColor = vec4(color,vColor.a);
}
