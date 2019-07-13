/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 Division Industries LLC
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

import java.util.ArrayList;
import java.util.List;

public enum KnownVersion {

    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1;

    public KnownVersion[] getVersionsBefore() {
        List<KnownVersion> versions = new ArrayList<>();

        for (KnownVersion v : values()) {
            if (v.equals(this)) break;
            versions.add(v);
        }

        return versions.toArray(new KnownVersion[versions.size()]);
    }

//    public KnownVersions[] getVersionsAfter() {
//        List<KnownVersions> versions = new ArrayList<>();
//
//        boolean shouldAdd = false;
//        for (KnownVersions v : values()) {
//            if (v.equals(this)) shouldAdd = true;
//            if (shouldAdd) versions.add(v);
//        }
//
//        return versions.toArray(new KnownVersions[versions.size()]);
//    }

    /**
     * Checks if the current server version is before the current instances version
     *
     * @return result
     */
    public boolean isBefore() {
        return equalsAny(NMSReflector.VERSION, getVersionsBefore());
    }

//    /**
//     * Inverse of the isBefore method
//     *
//     * @return result
//     */
//    public boolean isAfter() {
//        return equalsAny(NMSReflector.VERSION, getVersionsAfter());
//    }

    public static boolean equalsAny(String in, KnownVersion... cmp) {
        for (KnownVersion c : cmp) {
            if (in.equals(c.name())) return true;
        }

        return false;
    }
}
