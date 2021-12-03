#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;
const float GAMMA = 2.2;
const vec3 GAMMA_CORRECTION = vec3(1.0/GAMMA);

in vec3 mvVertexPos;
in vec3 mvVertexNormal;

out vec4 fragColor;

// insert light.glsl
#LIGHTS

//uniform sampler2D texture_sampler;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

uniform int numPointLights;
uniform int numSpotLights;

void main() {

    vec3 viewDir = normalize(-mvVertexPos);
    vec3 combined = calcDirLight(directionalLight, material, mvVertexPos,viewDir,mvVertexNormal);

    for(int i = 0; i < numPointLights; i++) {
        combined += calcPointLight(pointLights[i], material, mvVertexPos, viewDir, mvVertexNormal);
    }
    for(int i = 0; i < numSpotLights; i++) {
        combined += calcSpotLight(spotLights[i], material, mvVertexPos, viewDir, mvVertexNormal);
    }
    combined = pow(combined,GAMMA_CORRECTION);
    fragColor = vec4(combined,material.alpha);
}
