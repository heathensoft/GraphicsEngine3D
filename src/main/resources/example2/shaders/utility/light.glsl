
// author: Frederik Dahl

struct Attenuation {
    float c;
    float l;
    float q;
};

struct PointLight {
    vec3 a;
    vec3 d;
    vec3 s;
    vec3 pos;
    Attenuation att;
};

struct SpotLight {
    vec3 coneDir;
    float cutoffI;
    float cutoffO;
    PointLight pl;
};

struct DirectionalLight {
    vec3 a;
    vec3 d;
    vec3 s;
    vec3 dir;
};

struct Material {
    vec3 a;
    vec3 d;
    vec3 s;
    vec3 e;
    float shine;
};

vec3 a_color;
vec3 d_color;
vec3 s_color;
vec3 e_color;
float shine;

vec3 combined;

// vec3 viewDir = normalize(-fragPos);
// vec3 viewdir = normalize(camera_pos - fragpos); -> -fragpos (view space)

const float PI = 3.14159265;

vec3 calcDirLight(DirectionalLight light, vec3 eye, vec3 norm) {

    vec3 lightDir = normalize(light.dir);

    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    vec3 a = light.a * a_color;
    vec3 d = light.d * d_color * diff;
    vec3 s = light.s * s_color * spec;

    return (a + d + s);
}

vec3 calcPointLight(PointLight light, vec3 pos, vec3 eye, vec3 norm) {

    vec3 lightVec = light.pos - pos;
    vec3 lightDir = normalize(lightVec);

    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    float d = length(lightVec);
    float att = 1.0 / (light.att.c + light.att.l * d + light.att.q * d * d);

    vec3 a = light.a * a_color;
    vec3 d = light.d * d_color * diff;
    vec3 s = light.s * s_color * spec;

    a *= att;
    d *= att;
    d *= att;

    return (a + d + d);
}

vec3 calcSpotLight(SpotLight spotlight, vec3 pos, vec3 eye, vec3 norm) {

    PointLight light = spotlight.pl;
    vec3 lightVec = light.pos - pos;
    vec3 lightDir = normalize(lightVec);

    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    float theta = dot(-lightDir, normalize(-spotlight.coneDir)); // might need to inverse this
    float epsilon = (spotlight.cutoffI - spotlight.cutoffO);
    float intensity = clamp((theta - spotlight.cutoffO) / epsilon, 0.0, 1.0);

    float d = length(lightVec);
    float att = 1.0 / (light.att.c + light.att.l * d + light.att.q * d * d);

    vec3 a = light.a * a_color;
    vec3 d = light.d * d_color * diff;
    vec3 s = light.s * s_color * spec;

    a *= att * intensity;
    d *= att * intensity;
    s *= att * intensity;

    return (a + d + s);
}
