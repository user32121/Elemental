#version 330 compatibility

const vec3 pulse_col = vec3(0, 1, 0);
const float radar_cooldown = 5;

uniform sampler2D gtexture;
uniform bool pulse_enabled;
uniform float frameTimeCounter;

in vec2 texcoord;
in vec4 glcolor;
in vec4 position;

/* RENDERTARGETS: 0 */
layout(location = 0) out vec4 color;

void main() {
  if (!pulse_enabled) {
    discard;
  }
  color = texture(gtexture, texcoord) * glcolor;
  float frag_dist = length(position.rgb);
  float pulse_dist = mod(frameTimeCounter, radar_cooldown) * 20;
  float intensity = frag_dist <= pulse_dist ? pow(1.1, frag_dist - pulse_dist) *
                                                  3 / pow(1.05, pulse_dist)
                                            : 0;
  color.rgb = pulse_col;
  color.a *= intensity;
}