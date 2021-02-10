package me.infinity.utils;

public class UpdateUtil {

    public boolean canUpdate() {
        return Helper.getPlayer() != null && Helper.getWorld() != null;
    }
}