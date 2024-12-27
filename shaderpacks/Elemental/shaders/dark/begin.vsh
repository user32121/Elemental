#version 430 compatibility

#include "/include/common.glsl"

uniform bool holding_radar;
uniform int worldTime;
uniform int radar_cooldown;

int proper_modulo(int a, int b) { return (a % b + b) % b; }

void main() {
  if (holding_radar &&
      proper_modulo((worldTime - pulse_start_times[NUM_PULSES - 1]), 24000) >=
          radar_cooldown) {
    for (int i = 0; i < NUM_PULSES - 1; ++i) {
      pulse_start_times[i] = pulse_start_times[i + 1];
    }
    pulse_start_times[NUM_PULSES - 1] = worldTime;
  }
}