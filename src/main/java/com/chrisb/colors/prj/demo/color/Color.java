package com.chrisb.colors.prj.demo.color;

public enum Color {
    BLACK, WHITE, RED, GREEN, BLUE, PURPLE, ORANGE;

    public static boolean isValidColor(String color) {
        try {
            Color.valueOf(color);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
