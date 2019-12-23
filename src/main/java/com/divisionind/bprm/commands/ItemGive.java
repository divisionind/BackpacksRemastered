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

package com.divisionind.bprm.commands;

import com.divisionind.bprm.ACommand;
import com.divisionind.bprm.BackpackObject;
import com.divisionind.bprm.BackpackRecipes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemGive extends ACommand {

    public static final String KEY_NAME = "KEY";

    @Override
    public String alias() {
        return "item:give";
    }

    @Override
    public String desc() {
        return "gives the current player the backpack by type";
    }

    @Override
    public String usage() {
        return "<type> <player:none>";
    }

    @Override
    public String permission() {
        return "backpacks.item.give";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player p;

        // attempt to resolve player object
        if (args.length == 2) {
            p = validatePlayer(sender);
        } else
        if (args.length == 3) {
            p = Bukkit.getPlayer(args[2]);
            if (p == null) {
                respondf(sender, "&cPlayer \"%s\" was not found. Please check your spelling and try again.", args[2]);
                return;
            }
        } else {
            respondiu(sender, label);
            return;
        }

        // attempt to resolve backpack type
        BackpackObject item = BackpackObject.getByName(args[1]);
        if (item == null) {
            if (KEY_NAME.equalsIgnoreCase(args[1])) {
                p.getInventory().addItem(BackpackRecipes.BACKPACK_KEY);
                respond(sender, "&eGave the player a backpack key.");
            } else {
                respondf(sender, "&cBackpack of type \"%s\" was not found.", args[1]);
            }
            return;
        }

        p.getInventory().addItem(item.getItem());
        respondf(sender, "&eGave the player a %s backpack.", item.name().toLowerCase());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            List<String> parts = new ArrayList<>();
            if (args[1].equals("")) {
                for (BackpackObject item : BackpackObject.values()) parts.add(item.name());
                parts.add(KEY_NAME);
            } else {
                String typedLowercase = args[1].toLowerCase();
                for (BackpackObject item : BackpackObject.values()) {
                    if (item.name().toLowerCase().startsWith(typedLowercase)) parts.add(item.name());
                }

                if (KEY_NAME.toLowerCase().startsWith(typedLowercase)) parts.add(KEY_NAME);
            }
            return parts;
        } else return null;
    }
}