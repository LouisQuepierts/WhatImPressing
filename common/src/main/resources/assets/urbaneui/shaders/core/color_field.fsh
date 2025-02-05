#version 150

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    fragColor.a = 1.0;
    fragColor.rgb = mix(mix(vec3(1), vertexColor.rgb, texCoord0.x), vec3(0), texCoord0.y);
}
