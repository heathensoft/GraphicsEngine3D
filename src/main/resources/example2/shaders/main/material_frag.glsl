#version 420

const float GAMMA = 2.2;
const vec3 GAMMA_CORRECTION = vec3(1.0/GAMMA);

in VS_OUT {
    vec3 pos;
    vec3 nor;
} _in;

out vec4 color;

vec3 a_color;
vec3 d_color;
vec3 s_color;
vec3 e_color;
float shine;
float alpha;

#LIGHTS

#MATERIALS

#LIGHT_BLOCK

#MATERIAL_BLOCK

// INSTANCE
uniform int u_material_index;


void setupColors() {



    Material m = ubo_materials.list[u_material_index];
    a_color = m.ambient;
    d_color = m.diffuse;
    s_color = m.specular;
    e_color = m.diffuse * m.emission;
    shine = m.shine;
    alpha = m.alpha;




    /*
    a_color = vec3(1.0,0.8,1.0);
    d_color = a_color * 0.6;
    s_color = a_color * 0.1;
    e_color = a_color * 0;
    shine = 50.0;
    alpha = 1.0;
    */

}

void main() {

    setupColors();

    vec3 eye = normalize(-_in.pos);
    vec3 combined = vec3(0.0,0.0,0.0);

    for(int i = 0; i < ubo_lights.num_dir_lights; i++){
        DirectionalLight dirLight = ubo_lights.dirLights[i];
        combined += calc_dir_light(dirLight, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_point_lights; i++){
        PointLight pointLight = ubo_lights.pointLights[i];
        combined += calc_point_light(pointLight, _in.pos, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_spot_lights; i++){
        SpotLight spotLight = ubo_lights.spotLights[i];
        combined += calc_spot_light(spotLight, _in.pos, eye, _in.nor);
    }
    combined += e_color;
    // Gamma correction must be applied in the last shader only (last framebuffer)
    combined = pow(combined,GAMMA_CORRECTION);
    color = vec4(combined,alpha);
    //color = vec4(1,1,1,1);

}
