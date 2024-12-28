#version 430 compatibility

#include "/include/common.glsl"

uniform sampler2D gtexture;
uniform int worldTime;
uniform vec3 pulse_col = vec3(0, 1, 0);

in vec2 texcoord;
in vec4 glcolor;
in vec4 position;

layout(location = 0) out vec4 color;

float getIntensityFromPulse(float frag_dist, float pulse_start_time) {
  float pulse_dist = worldTime - pulse_start_time;  // 1 block per tick
  float intensity = frag_dist <= pulse_dist ? pow(1.1, frag_dist - pulse_dist) *
                                                  3 / pow(1.04, pulse_dist)
                                            : 0;
  return intensity;
}

void main() {
  color = texture(gtexture, texcoord) * glcolor;
  float frag_dist = length(position.xyz);
  float intensity = 0;
  for (int i = 0; i < NUM_PULSES; ++i) {
    intensity += getIntensityFromPulse(frag_dist, pulse_start_times[i]);
  }

  color.rgb = pulse_col;
  color.a *= intensity;
}