package net.quepierts.urbaneui;

import net.minecraft.util.ARGB;

public class ColorHelper {
    private static final int[] SPECTRUM = {
            0xffff0000,
            0xffffff00,
            0xff00ff00,
            0xff00ffff,
            0xff0000ff,
            0xffff00ff,
            0xffff0000
    };

    public static int toRGB(float hue, float saturation, float brightness) {
        if (saturation == 0f) {
            int gray = (int) (brightness * 255);
            return ARGB.color(0xff, gray, gray, gray);
        }

        float range = hue / 60.0f;
        int index = (int) range;
        float epsilon = range - index;

        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * epsilon);
        float t = brightness * (1.0f - saturation * (1.0f - epsilon));

        float r, g, b;
        switch (index) {
            case 0:
                r = brightness;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = brightness;
                b = p;
                break;
            case 2:
                r = p;
                g = brightness;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = brightness;
                break;
            case 4:
                r = t;
                g = p;
                b = brightness;
                break;
            case 5:
            default:
                r = brightness;
                g = p;
                b = q;
                break;
        }

        return ARGB.color(
                0xff,
                (int) (r * 255 + 0.5f),
                (int) (g * 255 + 0.5f),
                (int) (b * 255 + 0.5f)
        );
    }
    
    public static HSVColor toHSV(int argb) {
        int red = (argb >> 16) & 0xff;
        int green = (argb >> 8) & 0xff;
        int blue = argb & 0xff;

        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float hue = 0.0f;
        float saturation = 0.0f;
        float brightness = max;

        if (max != 0) {
            saturation = delta / max;
        }

        if (delta != 0) {
            if (max == r) {
                hue = (g - b) / delta;
            } else if (max == g) {
                hue = 2.0f + (b - r) / delta;
            } else {
                hue = 4.0f + (r - g) / delta;
            }
            hue *= 60.0f; // 转换为角度（0-360）
            if (hue < 0) hue += 360.0f;
        }

        return new HSVColor(hue, saturation, brightness);
    }

    public static int interpolate(int scolor, int dcolor, float value) {
        // 约束 value 在 [0.0, 1.0] 范围内
        float t = Math.max(0.0f, Math.min(1.0f, value));

        // 分解起始颜色（scolor）的ARGB通道
        int sA = (scolor >> 24) & 0xFF; // Alpha
        int sR = (scolor >> 16) & 0xFF; // Red
        int sG = (scolor >> 8) & 0xFF;  // Green
        int sB = scolor & 0xFF;         // Blue

        // 分解目标颜色（dcolor）的ARGB通道
        int dA = (dcolor >> 24) & 0xFF;
        int dR = (dcolor >> 16) & 0xFF;
        int dG = (dcolor >> 8) & 0xFF;
        int dB = dcolor & 0xFF;

        // 对每个通道进行线性插值
        int iA = Math.round(sA + (dA - sA) * t);
        int iR = Math.round(sR + (dR - sR) * t);
        int iG = Math.round(sG + (dG - sG) * t);
        int iB = Math.round(sB + (dB - sB) * t);

        // 确保通道值在 0-255 范围内（因浮点运算可能溢出）
        iA = Math.clamp(iA, 0, 255);
        iR = Math.clamp(iR, 0, 255);
        iG = Math.clamp(iG, 0, 255);
        iB = Math.clamp(iB, 0, 255);

        // 重新组合为 ARGB 颜色
        return (iA << 24) | (iR << 16) | (iG << 8) | iB;
    }

    public static int getHueColor(int section) {
        return SPECTRUM[section];
    }

    public static int getHueColor(float hue) {
        int section = Math.clamp((int) (hue / 60), 0, 5);
        return ColorHelper.interpolate(SPECTRUM[section], SPECTRUM[section + 1], (hue - section * 60f) / 60f);
    }

    public record HSVColor(
            float hue,
            float saturation,
            float brightness
    ) {}
}
