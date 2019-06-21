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

package com.divisionind.bprm;

import org.bukkit.plugin.java.JavaPlugin;

public class Backpacks extends JavaPlugin {

    public static final String VERSION = "@DivisionVersion@";
    public static final String GIT_HASH = "@DivisionGitHash@";
    public static final String GIT_NUM = "@DivisionGitComm@";

    @Override
    public void onEnable() {
        getLogger().info(String.format("Backpacks v%s (git: %s) has been enabled!", VERSION, GIT_HASH));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("Backpacks v%s (git: %s) has been disabled.", VERSION, GIT_HASH));
    }
}
