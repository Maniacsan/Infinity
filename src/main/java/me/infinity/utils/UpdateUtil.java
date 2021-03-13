package me.infinity.utils;

public class UpdateUtil {

    public static boolean canUpdate() {
        return Helper.getPlayer() != null && Helper.getWorld() != null;
    }
}