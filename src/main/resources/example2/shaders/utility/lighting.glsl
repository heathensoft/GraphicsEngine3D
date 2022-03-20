
struct Material {
    vec3 ambient;
    float emission;
    vec3 diffuse;
    float alpha;
    vec3 specular;
    float shine;
};

struct DirectionalLight {
    vec3 color;
    float ambient;
    vec3 direction;
    float diffuse;
};

struct PointLight {
    vec3 color;
    float farPlane;
    vec3 position;
    float ambient;
    float diffuse;
    float constant;
    float linear;
    float quadratic;
};

struct SpotLight {
    vec3 color;
    float ambient;
    vec3 position;
    float diffuse;
    vec3 coneDir;
    float constant;
    float linear;
    float quadratic;
    float innerCutoff;
    float outerCutoff;
};

vec3 a_source;
vec3 d_source;
vec3 s_source;
vec3 e_source;

float ec;
float shine;
float alpha;
float shadow;

vec3 a_sum = vec3(.0,.0,.0);
vec3 d_sum = vec3(.0,.0,.0);
vec3 s_sum = vec3(.0,.0,.0);
vec3 e_sum = vec3(.0,.0,.0);


float energyConservation(float shine) {
    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );
}

float calc_shadow(vec4 pos, vec3 norm, vec3 dir, sampler2D shadowMap) {
    vec3 coords = pos.xyz / pos.w;
    float shadowFactor = 0.0;
    float bias = max(0.05 * (1.0 - dot(norm, dir)), 0.005);
    vec2 inc = 1.0 / textureSize(shadowMap,0);
    coords = coords * 0.5 + 0.5;
    for(int r = -2; r <= 2; ++r){
        for(int c = -2; c <= 2; ++c){
            float textDepth = texture(shadowMap, coords.xy + vec2(r,c) * inc).r;
            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 25.0;
    if(coords.z > 1.0) {
        shadowFactor = 0.0;
    }
    return (1 - shadowFactor);
}

float calc_shadow(vec4 pos, sampler2D shadowMap) {
    vec3 coords = pos.xyz / pos.w;
    float shadowFactor = 0.0;
    float bias = 0.0001;
    vec2 inc = 1.0 / textureSize(shadowMap,0);
    coords = coords * 0.5 + 0.5;
    for(int r = -2; r <= 2; ++r){
        for(int c = -2; c <= 2; ++c){
            float textDepth = texture(shadowMap, coords.xy + vec2(r,c) * inc).r;
            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 25.0;
    if(coords.z > 1.0) {
        shadowFactor = 0.0;
    }
    return (1 - shadowFactor);
}

void calc_dirlight(DirectionalLight l, vec3 eye, vec3 norm) {
    vec3 toLightDir = -normalize(l.direction);
    float diff = max(dot(norm, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    a_sum += (l.color * a_source * l.ambient);
    d_sum += (l.color * d_source * l.diffuse * diff);
    s_sum += (l.color * s_source * spec);
}

void calc_pointlight(PointLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(norm, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    a_sum += (l.color * a_source * l.ambient) * att;
    d_sum += (l.color * d_source * l.diffuse * diff) * att;
    s_sum += (l.color * s_source * spec) * att;
}

void calc_spotlight(SpotLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(norm, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    float theta = dot(toLightDir, normalize(-l.coneDir));
    float epsilon = (l.innerCutoff - l.outerCutoff);
    float intensity = clamp((theta - l.outerCutoff) / epsilon, 0.0, 1.0);
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    a_sum += (l.color * a_source * l.ambient) * att * intensity ;
    d_sum += (l.color * d_source * l.diffuse * diff) * att * intensity * shadow;
    s_sum += (l.color * s_source * spec) * att * intensity * shadow;
}


