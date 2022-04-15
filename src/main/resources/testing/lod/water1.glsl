
const vec2 add = vec2(1.0, 0.0);
const vec2 addz = vec2(0.0, 1.0);
#define HASHSCALE1 .1031
#define SPEED 25.

float Hash12(vec2 p)
{
    vec3 p3  = fract(vec3(p.xyx) * HASHSCALE1);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

const mat2 m2 = mat2( 0.60, -0.80, 0.80, 0.60 );


float Noise(vec2 x )
{
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);

    float res = mix(mix( Hash12(p), Hash12(p + add.xy),f.x),
    mix( Hash12(p + add.yx), Hash12(p + add.xx),f.x),f.y);
    return res;
}

float FractalNoise(vec2 xy, float iTime)
{
    float w = .7;
    float f = 0.0;

    for (int i = 0; i < 4; i++)
    {
        f += Noise(xy+iTime*0.015*SPEED*w) * w;
        w *= 0.5;
        xy *= 2.7;
    }
    return f;
}

float waterMap(vec2 pos, float iTime) {
    vec2 posm = pos * m2;

    return abs( FractalNoise( vec2( 8.*posm), iTime)-0.5 )* 0.1;
}

vec3 aces_tonemap(vec3 color){
    mat3 m1 = mat3(
    0.59719, 0.07600, 0.02840,
    0.35458, 0.90834, 0.13383,
    0.04823, 0.01566, 0.83777
    );
    mat3 m2 = mat3(
    1.60475, -0.10208, -0.00327,
    -0.53108,  1.10813, -0.07276,
    -0.07367, -0.00605,  1.07602
    );
    vec3 v = m1 * color;
    vec3 a = v * (v + 0.0245786) - 0.000090537;
    vec3 b = v * (0.983729 * v + 0.4329510) + 0.238081;
    return pow(clamp(m2 * (a / b), 0.0, 1.0), vec3(1.0 / 2.2));
}

