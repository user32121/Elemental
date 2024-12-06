#version 330 compatibility

uniform mat4 gbufferModelViewInverse;
uniform bool pulse_enabled;

out vec2 lmcoord;
out vec2 texcoord;
out vec4 glcolor;
out vec3 normal;
out vec4 position;

void main() {
  gl_Position = ftransform();
  if (pulse_enabled) {
    gl_Position.z = 0.0;
  }
  texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
  lmcoord = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
  glcolor = gl_Color;
  normal = gl_NormalMatrix * gl_Normal;
  normal = mat3(gbufferModelViewInverse) * normal;
  position = gl_Vertex;
}