#version 430 compatibility

out vec2 texcoord;
out vec4 glcolor;
out vec4 position;

void main() {
  gl_Position = ftransform();
  gl_Position.z = 0.0;
  texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
  glcolor = gl_Color;
  position = gl_Vertex;
}