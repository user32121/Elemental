#define NUM_PULSES 4

layout(std430, binding = 0) buffer radar_state {
  int[NUM_PULSES] pulse_start_times;
};
