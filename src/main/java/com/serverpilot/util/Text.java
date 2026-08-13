package com.serverpilot.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Text {

    public static String bytes(long value) {
        if (value < 1024L) {
            return value + " B";
        }
        String[] units = {"KB", "MB", "GB", "TB"};
        double scaled = value;
        int unit = -1;
        do {
            scaled /= 1024.0;
            unit++;
        } while (scaled >= 1024.0 && unit < units.length - 1);
        return String.format(Locale.ROOT, "%.1f %s", scaled, units[unit]);
    }

    public static String duration(Duration duration) {
        long seconds = Math.max(0, duration.getSeconds());
        long days = seconds / 86_400;
        long hours = (seconds % 86_400) / 3_600;
        long minutes = (seconds % 3_600) / 60;
        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        }
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        }
        return seconds + "s";
    }

    public static String coordinates(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.2f, %.2f, %.2f", x, y, z);
    }

    public static String blockCoordinates(int x, int y, int z) {
        return x + ", " + y + ", " + z;
    }

    public static String decimal(double value, int places) {
        return String.format(Locale.ROOT, "%." + places + "f", value);
    }

    public static String prettify(String constant) {
        String[] words = constant.toLowerCase(Locale.ROOT).split("_");
        StringBuilder out = new StringBuilder(constant.length());
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!out.isEmpty()) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(word.charAt(0))).append(word, 1, word.length());
        }
        return out.toString();
    }

    public static List<String> wrap(String text, int width, int maxLines) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            if (word.isEmpty()) {
                continue;
            }
            if (!current.isEmpty() && current.length() + 1 + word.length() > width) {
                lines.add(current.toString());
                current.setLength(0);
                if (lines.size() == maxLines) {
                    return lines;
                }
            }
            if (!current.isEmpty()) {
                current.append(' ');
            }
            current.append(word);
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    public static String truncate(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, Math.max(0, max - 1)) + "…";
    }

    private Text() {
    }
}
