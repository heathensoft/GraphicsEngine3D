#version 330

const int MAX_P_LIGHTS  = 5;
const int MAX_S_LIGHTS  = 5;

in vec3 in_pos;
in vec3 in_nor;

out vec3 f_color;

#LIGHTS

// INSTANCE
uniform Material u_material;

// RENDER CYCLE
uniform PointLight u_pointLights[MAX_P_LIGHTS];
uniform SpotLight u_spotLights[MAX_S_LIGHTS];
uniform DirectionalLight u_directionalLight;
uniform int u_numPL;
uniform int u_numSL;

void setupColors() {

    a_color = u_material.a;
    d_color = u_material.d;
    s_color = u_material.s;
    e_color = u_material.e;
    shine = u_material.shine;
}

void main() {

    setupColors();

    vec3 eye = normalize(-in_pos);

    combined = calcDirLight(u_directionalLight, eye, in_nor);

    for(int i = 0; i < u_numPL; i++){
        combined += calcPointLight(u_pointLights[i], in_pos, eye, in_nor);
    }
    for(int i = 0; i < u_numSL; i++){
        combined += calcSpotLight(u_spotLights[i], in_pos, eye, in_nor);
    }
    combined += e_color;
    // Gamma correction must be applied in the last shader only (last framebuffer)
    //combined = pow(combined,GAMMA_CORRECTION);
    fragColor = vec4(combined,1.0);

}
