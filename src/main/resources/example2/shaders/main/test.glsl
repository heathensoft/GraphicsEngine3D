#version 330

// Frederik Dahl (2021)

// *****************************************************************************

const float GAMMA           = 2.2;
const vec3 GAMMA_CORRECTION = vec3(1.0/GAMMA);

const int MAX_POINT_LIGHTS  = 5;
const int MAX_SPOT_LIGHTS   = 5;

const vec3 AMBIENT_DEFAULT  = vec3(0.5,0.5,0.5);
const vec3 DIFFUSE_DEFAULT  = vec3(1.0,1.0,1.0);
const vec3 EMISSIVE_DEFAULT = vec3(0.0,0.0,0.0);
const vec3 SPECULAR_DEFAULT = vec3(0.1,0.1,0.1);
const float DEFAULT_SHINE   = 0.1;
const float DEFAULT_ALPHA   = 1.0;

const int RAW_MATERIAL      = 1;
const int USE_PALETTE       = 2;
const int SINGLE_TEXEL      = 4;
const int DIFFUSE_ONLY      = 8;
const int USE_MIXING        = 16;
const int IS_TERRAIN        = 32;
const int USE_NORMAL_MAP    = 64;
const int TRANSPARENCY      = 128;

// *****************************************************************************

// in_my is unprojected y used for coloring heightmaps

in float in_my;
in vec3 in_mvpos;
in vec3 in_normal;
in vec2 in_uv;
flat in int in_texel;

// *****************************************************************************

out vec4 out_color;

// *****************************************************************************

#LIGHTS

// *****************************************************************************

// uploded once

uniform int u_numPaletteColors;
uniform sampler1D u_palette;

uniform sampler2D u_secondary[8];
uniform sampler2D u_textures[8];

//uniform int terrainColors[NUM_TERRAIN_COLORS];

// *****************************************************************************

// uploaded each render cycle

uniform PointLight u_pointLights[MAX_POINT_LIGHTS];
uniform SpotLight u_spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight u_directionalLight;
uniform int u_numPointLights;
uniform int u_numSpotLights;

//uniform sampler2D shadowMap;

// *****************************************************************************

// uploaded for each instance

uniform int u_flags;                // Required
uniform int u_textureSlots;         // Not required
uniform float u_alpha;              // Not required
uniform float u_shine;              // Not required
uniform float u_secondaryWeight;    // Not required
uniform Material u_material;        // Not required

// *****************************************************************************




void main() {

    // The final sum of all light components
    vec3 combined = vec3(0,0,0);

    // Initiates light data as shader default light data
    vec3 ambientData = vec3(AMBIENT_DEFAULT);
    vec3 diffuseData = vec3(DIFFUSE_DEFAULT);
    vec3 specularData = vec3(SPECULAR_DEFAULT);
    vec3 emmisiveData = vec3(EMISSIVE_DEFAULT);
    float shine = DEFAULT_SHINE;
    float alpha = DEFAULT_ALPHA;

    // Defines the primary and secondary(mix) texture contributions
    // from the textureSlots uniform
    int primarySlot = (u_textureSlots & 0x0F);
    int secondarySlot = (u_textureSlots & 0xF0);

    // Defines the flags from the flags uniform
    bool rawMaterial = (u_flags & RAW_MATERIAL) == RAW_MATERIAL;
    bool usePalette = (u_flags & USE_PALETTE) == USE_PALETTE;
    bool diffuseOnly = (u_flags & DIFFUSE_ONLY) == DIFFUSE_ONLY;
    bool useMixing = (u_flags & USE_MIXING) == USE_MIXING;
    bool isTerrain = (u_flags & IS_TERRAIN) == IS_TERRAIN;

    // todo: Check if alpha = 0.0. Then, no need for calculations

    if(rawMaterial) {

        ambientData = u_material.ambient;
        diffuseData = u_material.diffuse;
        specularData = u_material.specular;
        emmisiveData = u_material.emmisive;
        shine = u_material.shine;
        alpha = u_material.alpha;

        // this requires texcoords and uniform secondaryWeight

        if(useMixing) {

            vec3 mixColor = texture(u_secondary[secondarySlot],in_uv).rgb;
            diffuseData = mix(diffuseData,mixColor,u_secondaryWeight);
        }

    }

    else {

        if(usePalette) {

            int stripSize = u_numPaletteColors + 1;
            int modulo = mod(in_texel,stripSize); // try % later
            int offset = in_texel + u_numPaletteColors - modulo;

            diffuseData = texelFetch(u_palette,in_texel,0).rgb;
            vec3 ses = texelFetch(u_palette,offset).rgb;
            specualarData = ses.rrr;
            emmisiveData = ses.ggg;
            shine = ses.b;

        }
        else {

            // Treat the texture as a single texture region
            // Use the texture coordinates as they are
            if(diffuseOnly) {

                diffuseData = texture(u_textures[primarySlot],in_uv).rgb;
                ambientData = diffuseData;

            }

            // Treat the texture as a 2x2 texture region.
            // We translate the texCoords accordingly.
            // And we sample the lightingMap 3 times:
            // Diffuse region
            // Specular region
            // Emissive region
            // Later we can add a normal map region;
            else {

                vec2 uv_diff = in_uv * 0.5;
                diffuseData = texture(u_textures[primarySlot],uv_diff).rgb;
                //vec2 uv_spec = uv_diff + vec2()

            }

        }

    }


    Material material = Material(ambientData,diffuseData,specularData,emmisiveData,shine,alpha);

    vec3 viewDir = normalize(-in_mvpos);
    vec3 combined = calcDirLight(u_directionalLight,material,in_mvpos,viewDir,in_normal);

    for(int i = 0; i < numPointLights; i++){
        combined += calcPointLight(u_pointLights[i],material,in_mvpos,viewDir,in_normal);
    }
    for(int i = 0; i < numSpotLights; i++){
        combined += calcSpotLight(u_spotLights[i],material,in_mvposs,viewDir,in_normal);
    }


    combined += emmisiveData;
    // Gamma correction must be applied in the last shader only (last framebuffer)
    combined = pow(combined,GAMMA_CORRECTION);
    out_color = vec4(combined,alpha);

}
