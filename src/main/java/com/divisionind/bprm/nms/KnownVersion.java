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

import com.divisionind.bprm.nms.reflect.NMS;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of known versions. This allows us to get the versions before our version and is useful
 * for when NMS changes. Note: We can NOT reliably get the versions after a particular version
 * because those are always changing.
 */
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

    /**
     * Checks if the current server version is before the current instances version
     *
     * @return result
     */
    public boolean isBefore() {
        return equalsAny(NMS.VERSION, getVersionsBefore());
    }

    public static boolean equalsAny(String in, KnownVersion... cmp) {
        for (KnownVersion c : cmp) {
            if (in.equals(c.name())) return true;
        }

        return false;
    }
}
