#version 400

in VS_OUT {
    vec3 pos;
    vec3 nor;
} _in;

out FS_OUT {
    vec3 color;
} _out;

#LIGHTS_STD140

#define MAX_DIR_LIGHTS 2
#define MAX_POINT_LIGHTS 20
#define MAX_SPOT_LIGHTS 10

layout(std140) uniform Lights {                 // example array size: 2 dir, 20 point, 10 spot
    DirectionalLight dirLights[MAX_DIR_LIGHTS]; // offset = 0,      stride = 2 * 32     = 64
    PointLight pointLights[MAX_POINT_LIGHTS];   // offset = 64,     stride = 20 * 48    = 960
    SpotLight spotLights[MAX_SPOT_LIGHTS];      // offset = 1024,   stride = 10 * 64    = 640
    int num_dir_lights;                         // offset = 1664,
    int num_point_lights;                       // offset = 1668,
    int num_spot_lights;                        // offset = 1672,
} ubo_lights;                                   // SIZE   = 1676,

#define MATERIAL_COUNT 10

layout(std140) uniform Materials {              // Static UBO
    Material list[MATERIAL_COUNT];              // SIZE = 480
} ubo_materials;

// INSTANCE
uniform int u_material_index;


void setupColors() {

    Material m = ubo_materials.list[u_material_index];
    a_color = m.ambient;
    d_color = m.diffuse;
    s_color = m.specular;
    e_color = m.diffuse * m.emissive;
    shine = m.shine;
    alpha = m.alpha;
}

void main() {

    setupColors();

    vec3 eye = normalize(-_in.pos);
    vec3 combined = vec3(0.0,0.0,0.0);

    for(int i = 0; i < ubo_lights.num_dir_lights; i++){
        DirectionalLight dirLight = ubo_lights.dirLights[i];
        combined += calcDirLight(dirLight, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_point_lights; i++){
        PointLight pointLight = ubo_lights.pointLights[i];
        combined += calcPointLight(pointLight, _in.pos, eye, _in.nor);
    }
    for(int i = 0; i < ubo_lights.num_spot_lights; i++){
        SpotLight spotLight = ubo_lights.spotLights[i];
        combined += calcSpotLight(spotLight, _in.pos, eye, _in.nor);
    }
    combined += e_color;
    // Gamma correction must be applied in the last shader only (last framebuffer)
    //combined = pow(combined,GAMMA_CORRECTION);
    _out.color = vec4(combined,alpha);

}
