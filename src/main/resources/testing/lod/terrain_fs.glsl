#version 430

#WATER

uniform float uTime;
uniform vec3 uLightDir;
uniform vec3 uCameraPos;
uniform sampler2D uTerrainDetail;

in vec2 uv;
in vec3 vertPos;
in vec3 vertNormal;
in vec3 vertColor;
in float height;
in float vertShine;
in float detailBlend;

out vec4 fColor;

// light
const float lightAmbient = 0.3;
const float lightDiffuse = 0.7;
const vec3 lightColor = vec3(0.9,0.9,0.9);

const vec3 speculr = vec3(0.4,0.3,0.3);
const float waterLevel = 8.0 / 32.0;

float energyConservation(float shine) {
    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );
}

vec3 NormalBlend_UDN(vec3 n1, vec3 n2, float d){
    return normalize(vec3(n1.xy + n2.xy/d, n1.z));
}

void main() {

    vec3 combined;

    if(height < waterLevel) {
        vec2 uv_water = -1.0 + 2.0 * (vertPos.xz/4096.0);
        vec2 nor;
        nor.x =  (waterMap(uv_water + add,uTime) - waterMap(uv_water - add,uTime) ) / (2. * .1);
        nor.y =  (waterMap(uv_water + addz,uTime) - waterMap(uv_water-addz,uTime) ) / (2. * .1);

        float water = waterMap(nor,uTime);
        vec3 col = mix(vec3(0.1,0.4,0.6),vertColor,0.5);
        col+=water;
        uv_water*=5.;

        float noise1 = (FractalNoise(uv_water,uTime) -.55) * 5.0;
        float noise2 = (FractalNoise(vec2(uv_water.x+0.25,uv_water.y+0.25),uTime) -.55) * 5.0;
        col = mix(col, aces_tonemap(vec3(0.65,0.65,0.75)), clamp((noise2*0.1-.1)/water, 0.0, 1.0)*0.1);
        col = mix(col, aces_tonemap(vec3(1.0, 1.0, 1.09)), clamp((noise1*0.1-.1)/water, 0.0, 1.0)*0.1);


        float shine = 40.0;
        // diffuse
        vec3 toLightDir = normalize(-uLightDir);
        float diff = max(dot(vertNormal, toLightDir), 0.0);
        // specular
        vec3 normal = normalize(vec3(nor.x,1,nor.y) + vec3(0.0,0.5,0.0));
        vec3 toEyeDir = normalize(uCameraPos - vertPos);
        vec3 halfwayDir = normalize(toLightDir + toEyeDir);
        float spec = pow(max(dot(normal,halfwayDir),0.0),shine);
        spec *= energyConservation(shine);
        vec3 mixed = mix(col,vertColor,0.5);
        vec3 a_sum = (lightColor * mixed * lightAmbient);
        vec3 d_sum = (lightColor * (vertColor + col) * lightDiffuse * diff);
        vec3 s_sum = (lightColor * col * spec);
        vec3 e_sum = vec3(0.0,0.0,0.0);
        combined = (e_sum + a_sum + d_sum + s_sum);
    }
    else {
        vec3 detailNormal = texture(uTerrainDetail,uv).rgb;
        detailNormal = normalize(detailNormal * 2.0 - 1);
        vec3 normal = NormalBlend_UDN(vertNormal,detailNormal,detailBlend).xzy;
        float shine = vertShine * 128.0;
        // diffuse
        vec3 toLightDir = normalize(-uLightDir);
        float diff = max(dot(normal, toLightDir), 0.0);
        // specular
        vec3 toEyeDir = normalize(uCameraPos - vertPos);
        vec3 halfwayDir = normalize(toLightDir + toEyeDir);
        float spec = pow(max(dot(normal,halfwayDir),0.0),shine);
        spec *= energyConservation(shine);
        vec3 a_sum = (lightColor * vertColor * lightAmbient);
        vec3 d_sum = (lightColor * vertColor * lightDiffuse * diff);
        vec3 s_sum = (lightColor * speculr * spec);
        vec3 e_sum = vec3(0.0,0.0,0.0);
        combined = (e_sum + a_sum + d_sum + s_sum);
    }

    fColor = vec4(combined,1.0);

}
