#version 300 es
precision mediump float;
out vec4 fragColor;
in vec4 fColor;
void main() {
    fragColor = fColor;
}