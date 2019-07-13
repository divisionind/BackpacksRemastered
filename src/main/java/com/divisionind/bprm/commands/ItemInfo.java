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

package com.divisionind.bprm.commands;

import com.divisionind.bprm.ACommand;
import com.divisionind.bprm.nms.NMSReflector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ItemInfo extends ACommand {
    @Override
    public String alias() {
        return "item:info";
    }

    @Override
    public String desc() {
        return "displays a list of NBT data for the item you are holding";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String permission() {
        return "backpacks.item.info";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player p = validatePlayer(sender);
        ItemStack item = p.getInventory().getItemInMainHand();

        try {
            Object tagCompound = NMSReflector.getNBTTagCompound(NMSReflector.asNMSCopy(item));
            // show all nbt data
            Set<String> data = NMSReflector.getKeys(tagCompound);
            for (String s : data) {
                respondn(sender, s);
            }
            respondf(sender, "&eFound %s NBT data entries.", data.size());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
