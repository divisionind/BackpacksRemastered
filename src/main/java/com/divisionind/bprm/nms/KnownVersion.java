/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019, Andrew Howard, <divisionind.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.divisionind.bprm.nms;

import org.bukkit.Bukkit;

/**
 * A list of known versions. This gives us a history of how one version progressed to the next which
 * is useful for quickly resolving NMS functionality at runtime. It's why we can support so many versions.
 *
 * This requires that our list of previous versions be comprehensive back to min_supported_version, but allows for
 * future unknown versions (not in the enum) to fall back to whatever the latest NMS revision is. Hence, why there
 * is no .after().
 */
public enum KnownVersion {

    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    ;

    private static final KnownVersion KVERSION;
    public static final String VERSION;

    static {
        String vtmp = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = vtmp.substring(vtmp.lastIndexOf('.') + 1);

        KnownVersion ktmp;
        try {
            ktmp = valueOf(VERSION);
        } catch (IllegalArgumentException e) {
            KnownVersion[] versions = values();
            ktmp = versions[versions.length-1];
        }
        KVERSION = ktmp;
    }

    /**
     * @return whether the current server version is before the current instance's version (non-inclusive)
     */
    public boolean before() {
        return KVERSION.ordinal() < ordinal();
    }
}
