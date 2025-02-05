package net.quepierts.urbaneui;

public class MathHelper {
    public static int clamp(int value, int min, int max) {
        return value > max ? max : value < min ? min : value;
    }

    public static float clamp(float value, float min, float max) {
        return value > max ? max : value < min ? min : value;
    }

    public static double clamp(double value, double min, double max) {
        return value > max ? max : value < min ? min : value;
    }
}
