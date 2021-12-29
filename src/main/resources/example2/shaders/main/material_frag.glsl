#version 430

const float GAMMA = 2.2;
const vec3 GAMMA_CORRECTION = vec3(1.0/GAMMA);

in VS_OUT {
    vec3 pos;
    vec3 nor;
    vec4 lpos;
} _in;

out vec4 color;

#LIGHTING

#LIGHT_BLOCK

#MATERIAL_BLOCK

// INSTANCE
uniform int u_material_index;
uniform sampler2D u_shadowMap;


void setupColors() {

    Material m = ubo_materials.list[u_material_index];
    a_source = m.ambient;
    d_source = m.diffuse;
    s_source = m.specular;
    e_source = m.diffuse * m.emission;
    shine = m.shine;
    alpha = m.alpha;
    shadow = calc_shadow(_in.lpos, u_shadowMap);
    ec = energyConservation(shine);

}


void main() {

    setupColors();

    vec3 eye = normalize(-_in.pos);

    for(int i = 0; i < ubo_lights.num_dir_lights; i++){
        DirectionalLight dirLight = ubo_lights.dirLights[i];
        calc_dirlight(dirLight, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_point_lights; i++){
        PointLight pointLight = ubo_lights.pointLights[i];
        calc_pointlight(pointLight, _in.pos, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_spot_lights; i++){
        SpotLight spotLight = ubo_lights.spotLights[i];
        calc_spotlight(spotLight, _in.pos, eye, _in.nor);
    }

    e_sum += e_source;

    //vec3 combined = clamp(e_sum + a_sum + (d_sum + s_sum) * shadow, 0, 1);

    vec3 combined = (e_sum + a_sum + d_sum + s_sum);
    color = vec4(combined,alpha);

}
