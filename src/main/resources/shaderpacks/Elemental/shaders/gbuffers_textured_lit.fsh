#version 330 compatibility

const vec3 blocklight_col = vec3(1.0, 0.5, 0.08);
const vec3 skylight_col = vec3(0.05, 0.15, 0.3);
const vec3 sunlight_col = vec3(1.0);
const vec3 ambient_col = vec3(0.1);
const vec3 pulse_col = vec3(0, 1, 0);

uniform sampler2D lightmap;
uniform sampler2D gtexture;
uniform vec3 shadowLightPosition;
uniform mat4 gbufferModelViewInverse;
uniform float alphaTestRef = 0.1;
uniform bool pulse_enabled;
uniform float frameTimeCounter;

in vec2 lmcoord;
in vec2 texcoord;
in vec4 glcolor;
in vec3 normal;
in vec4 position;

/* RENDERTARGETS: 0 */
layout(location = 0) out vec4 color;

void standard_lighting() {
  color = texture(gtexture, texcoord) * glcolor;
  color.rgb = pow(color.rgb, vec3(2.2));

  vec4 lightmapData = vec4(lmcoord.xy, 0.0, 1.0);
  vec3 blocklight = lightmapData.r * blocklight_col;
  vec3 skylight = lightmapData.g * skylight_col;

  vec3 ambient = ambient_col;

  vec3 lightVector = normalize(shadowLightPosition);
  vec3 worldLightVector = mat3(gbufferModelViewInverse) * lightVector;
  vec3 sunlight = sunlight_col *
                  clamp(dot(worldLightVector, normal), 0.0, 1.0) *
                  lightmapData.g;

  color.rgb *= blocklight + skylight + ambient + sunlight;

  color.rgb = pow(color.rgb, vec3(1.0 / 2.2));

  if (color.a < alphaTestRef) {
    discard;
  }
}

void pulse_lighting() {
  color = texture(gtexture, texcoord) * glcolor;
  float frag_dist = length(position.rgb);
  float pulse_dist = mod(frameTimeCounter, 3) * 20;
  float intensity = frag_dist <= pulse_dist ? pow(1.1, frag_dist - pulse_dist) *
                                                  3 / pow(1.1, pulse_dist)
                                            : 0;
  color.rgb = pulse_col;
  color.a *= intensity;
}

void main() {
  if (pulse_enabled) {
    pulse_lighting();
  } else {
    standard_lighting();
  }
}