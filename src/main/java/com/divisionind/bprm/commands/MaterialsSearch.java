/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 - 2020, Andrew Howard, <divisionind.com>
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

package com.divisionind.bprm.commands;

import com.divisionind.bprm.ACommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MaterialsSearch extends ACommand {
    @Override
    public String alias() {
        return "materials:search";
    }

    @Override
    public String desc() {
        return "searches available materials to return a list of results";
    }

    @Override
    public String usage() {
        return "<string>";
    }

    @Override
    public String permission() {
        return "backpacks.materials.search";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        validateArgsLength(args, 2);

        StringBuilder sb = new StringBuilder();
        List<Material> results = new ArrayList<>();

        for (Material mat : Material.values()) {
            if (mat.name().toLowerCase().contains(args[1].toLowerCase())) results.add(mat);
        }

        renderList(sb, results.toArray(new Material[results.size()]));
        ACommand.respond(sender, sb.toString());
    }

    public static void renderList(StringBuilder sb, Material[] mats) {
        sb.append("&eMaterials: &7");
        for (int i = 0;i<mats.length;i++) {
            sb.append(mats[i].name());
            if ((i + 1) != mats.length) sb.append(", ");
        }
    }
}
