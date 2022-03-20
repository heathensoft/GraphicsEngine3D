
struct DL {
    vec3 color;
    float ambient;
    vec3 directionView;
    float diffuse;
};

struct PL {
    vec3 color;
    float farPlane;
    vec3 positionView;
    float ambient;
    float diffuse;
    float constant;
    float linear;
    float quadratic;
};

struct SL {
    vec3 color;
    float ambient;
    vec3 positionView;
    float diffuse;
    vec3 directionView;
    float constant;
    float linear;
    float quadratic;
    float innerCutoff;
    float outerCutoff;
};

struct DLC {
    DL light;
    mat4 lightSpace;
};

struct PLC {
    PL light;
    vec4 positionWorld;
};

struct SLC {
    SL light;
    mat4 lightSpace;
};

layout(std140, binding = 0) uniform Lights {
    // floatBuffer
    DL dl[];
    PL pl[];
    SL sl[];
    // intbuffer
    int dl_count;
    int pl_count;
    int sl_count;
    int flags;
} ubo_lights;

layout(std140, binding = 0) uniform Casters {
    // floatBuffer
    DLC dl[];
    PLC pl[];
    SLC sl[];
    // intbuffer
    samplerCube[4] pl_depthMaps;
    sampler2D[4] dl_depthMaps;
    sampler2D[4] sl_depthMaps;
    int dl_count;
    int pl_count;
    int sl_count;
    int flags;
} ubo_casters;

// Input:

// Set theese appropriately bofore lighting calculations
vec3 a_source;
vec3 d_source;
vec3 s_source;
vec3 e_source;

float energyConservation;
float shine;

// Output:

// Colors summed up in lighting calculations. Used in final fragment coloring.
// .i.e. vec3 combined = (e_sum + a_sum + d_sum + s_sum);
vec3 a_sum = vec3(.0,.0,.0);
vec3 d_sum = vec3(.0,.0,.0);
vec3 s_sum = vec3(.0,.0,.0);
vec3 e_sum = vec3(.0,.0,.0);


// Used to smoothen point-light shadows.
vec3 gridSamplingDisk[20] = vec3[]
(
    vec3(1, 1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1, 1,  1),
    vec3(1, 1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1, 1, -1),
    vec3(1, 1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1, 1,  0),
    vec3(1, 0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1, 0, -1),
    vec3(0, 1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0, 1, -1)
);

// https://github.com/JoeyDeVries/LearnOpenGL/blob/master/src/5.advanced_lighting/3.2.2.point_shadows_soft/3.2.2.point_shadows.fs

float calc_shadow_cube(vec3 fragPos_worldSpace, vec3 lightPos_worldSpace, vec3 camPos_worldSpace, float farPlane, samplerCube depthMap) {
    // get vector between fragment position and light position
    vec3 fragToLight = fragPos_worldSpace - lightPos_worldSpace;
    // now get current linear depth as the length between the fragment and light position
    float currentDepth = length(fragToLight);
    float shadow = 0.0;
    float bias = 0.15;
    int samples = 20;
    // length of the distance between the camera (eye) and the fragment on the model
    float viewDistance = length(camPos_worldSpace - fragPos_worldSpace);
    float diskRadius = (1.0 + (viewDistance / farPlane)) / 25.0;

    for(int i = 0; i < samples; i++) // todo: Here we got i++ instead of ++i, is this correct?
    {
        float closestDepth = texture(depthMap, fragToLight + gridSamplingDisk[i] * diskRadius).r;
        closestDepth *= farPlane;   // undo mapping [0;1]
        if(currentDepth - bias > closestDepth){
            shadow += 1.0;
        }
    }
    shadow /= float(samples);
    return shadow; // todo: possibly (1 - shadow)
}

float calc_shadow_2D(vec4 fragPos_lightSpace, vec3 normal_viewSpace, vec3 lightDir_viewSpace, sampler2D depthMap) {
    vec3 coords = fragPos_lightSpace.xyz / fragPos_lightSpace.w;
    float shadowFactor = 0.0;
    float bias = max(0.05 * (1.0 - dot(normal_viewSpace, lightDir_viewSpace)), 0.005);
    vec2 inc = 1.0 / textureSize(depthMap,0);
    coords = coords * 0.5 + 0.5;
    for(int r = -2; r <= 2; ++r){
        for(int c = -2; c <= 2; ++c){
            float textDepth = texture(depthMap, coords.xy + vec2(r,c) * inc).r;
            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 25.0;
    if(coords.z > 1.0) {
        shadowFactor = 0.0;
    }
    return (1 - shadowFactor);
}

float energyConservation(float shine) {
    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );
}

// toEye is the normalized direction vector from fragPos to cameraPos in viewSpace
// So remember to normalize it before passing it in.
void calc_dl(DL dl, vec3 toEye_dir, vec3 normal_viewSpace) {
    vec3 toLightDir = -normalize(dl.directionView);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    a_sum += (dl.color * a_source * dl.ambient);
    d_sum += (dl.color * d_source * dl.diffuse * diff);
    s_sum += (dl.color * s_source * spec);
}

void calc_dlc(DLC dlc, vec3 toEye_dir, vec3 normal_viewSpace, float shadow) {
    DL dl = dlc.light;
    vec3 toLightDir = -normalize(dl.directionView);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    a_sum += (dl.color * a_source * dl.ambient);
    d_sum += (dl.color * d_source * dl.diffuse * diff) * shadow;
    s_sum += (dl.color * s_source * spec) * shadow;
}

void calc_pl(PL pl, vec3 pos_viewSpace, vec3 toEye_dir, vec3 normal_viewSpace) {
    vec3 lightVec = pl.positionView - pos_viewSpace;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    float d = length(lightVec);
    float att = 1.0 / (pl.constant + pl.linear * d + pl.quadratic * d * d);
    a_sum += (pl.color * a_source * pl.ambient) * att;
    d_sum += (pl.color * d_source * pl.diffuse * diff) * att;
    s_sum += (pl.color * s_source * spec) * att;
}

void calc_plc(PLC plc, vec3 pos_viewSpace, vec3 toEye_dir, vec3 normal_viewSpace, float shadow) {
    PL pl = plc.light;
    vec3 lightVec = pl.positionView - pos_viewSpace;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    float d = length(lightVec);
    float att = 1.0 / (pl.constant + pl.linear * d + pl.quadratic * d * d);
    a_sum += (pl.color * a_source * pl.ambient) * att;
    d_sum += (pl.color * d_source * pl.diffuse * diff) * att * shadow;
    s_sum += (pl.color * s_source * spec) * att * shadow;
}

void calc_sl(SL sl, vec3 pos_viewSpace, vec3 toEye_dir, vec3 normal_viewSpace) {
    vec3 lightVec = sl.positionView - pos_viewSpace;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    float theta = dot(toLightDir, normalize(-sl.directionView));
    float epsilon = (sl.innerCutoff - sl.outerCutoff);
    float intensity = clamp((theta - sl.outerCutoff) / epsilon, 0.0, 1.0);
    float d = length(lightVec);
    float att = 1.0 / (sl.constant + sl.linear * d + sl.quadratic * d * d);
    a_sum += (sl.color * a_source * sl.ambient) * att * intensity;
    d_sum += (sl.color * d_source * sl.diffuse * diff) * att * intensity;
    s_sum += (sl.color * s_source * spec) * att * intensity;
}

void calc_slc(SLC slc, vec3 pos_viewSpace, vec3 toEye_dir, vec3 normal_viewSpace, float shadow) {
    SL sl = slc.light;
    vec3 lightVec = sl.positionView - pos_viewSpace;
    vec3 toLightDir = normalize(lightVec);
    float diff = max(dot(normal_viewSpace, toLightDir), 0.0);
    vec3 halfwayDir = normalize(toLightDir + toEye_dir);
    float spec = pow(max(dot(normal_viewSpace,halfwayDir),0.0),shine) * energyConservation;
    float theta = dot(toLightDir, normalize(-sl.directionView));
    float epsilon = (sl.innerCutoff - sl.outerCutoff);
    float intensity = clamp((theta - sl.outerCutoff) / epsilon, 0.0, 1.0);
    float d = length(lightVec);
    float att = 1.0 / (sl.constant + sl.linear * d + sl.quadratic * d * d);
    a_sum += (sl.color * a_source * sl.ambient) * att * intensity ;
    d_sum += (sl.color * d_source * sl.diffuse * diff) * att * intensity * shadow;
    s_sum += (sl.color * s_source * spec) * att * intensity * shadow;
}