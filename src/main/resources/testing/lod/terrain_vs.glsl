#version 430

layout (location=0) in vec2 aPosition;
layout (location=1) in vec4 aInstance;

//uniform float uAmplitude;
uniform mat4 uCombined;
uniform sampler2D uHeightTexture;
uniform sampler2D uNormalTexture;
uniform sampler1D uTerrainPalette;
uniform sampler1D uTerrainSpecular;

out vec3 vertPos;
out vec3 vertColor;
out float vertShine;
out float detailBlend;
out vec3 vertNormal;
out float height;
out vec2 uv;
//out vec2 uv_water;


void main() {
    float x = aInstance.x;
    float z = aInstance.y;
    float s = aInstance.z;
    mat4 model = mat4(
    s,   0.0, 0.0, 0.0,  // 1. column
    0.0, 1.0, 0.0, 0.0,  // 2. column
    0.0, 0.0, s,   0.0,  // 3. column
    x,   0.0, z,   1.0); // 4. column
    vec4 modelPos = model * vec4(aPosition.x,0.0,aPosition.y,1.0);
    ivec2 texelPos = ivec2(modelPos.x,modelPos.z);
    height = texelFetch(uHeightTexture,texelPos,0).r;
    detailBlend = 3.0 / height * 2.0 + 1;
    vertShine = texture(uTerrainSpecular,height).r / 3.0;
    vertColor = texture(uTerrainPalette,height).rgb;
    uv = (vec2(modelPos.xz) / 4096.0) * 32.0;
    //height = (height * 2.0 - 1.0) * uAmplitude;
    /*
    float y = 0;
    if(height < (8.0/32.0)) {
        height = (height * 2.0 - 1.0);
        y = height * uAmplitude;
    }
    */
    vertNormal = texelFetch(uNormalTexture,texelPos,0).rgb;
    vertNormal = normalize(vertNormal * 2.0 - 1.0); // unpack from rgb
    vertPos = vec3(modelPos.x,0.0,modelPos.z);
    gl_Position = uCombined * vec4(vertPos,1.0);

}