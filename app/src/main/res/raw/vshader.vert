#version 300 es
in vec4 vPosition;
in vec3 vColor;
out vec4 fColor;
void main() {
    gl_Position = vPosition;
    fColor = vec4(vColor, 1.0f);
}