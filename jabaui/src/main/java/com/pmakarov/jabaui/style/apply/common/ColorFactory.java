package com.pmakarov.jabaui.style.apply.common;

import java.awt.*;

/**
 * @author pmakarov
 */
public final class ColorFactory {
    private ColorFactory() {

    }

    /**
     * Get Color instance by hex value
     *
     * @param hex hex value
     * @return Color instance
     */
    public static Color get(String hex) {
        if (null != hex && !hex.isEmpty() && hex.startsWith("#")) {
            return new Color(
                    Integer.valueOf(hex.substring(1, 3), 16),
                    Integer.valueOf(hex.substring(3, 5), 16),
                    Integer.valueOf(hex.substring(5, 7), 16));
        } else {
            throw new IllegalArgumentException("Expect hex format for color:" + hex);
        }
    }

    public static Color withOpacity(String hex, int opacity) {
        return withOpacity(ColorFactory.get(hex), opacity);
    }

    public static Color withOpacity(Color color, int opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }
}
