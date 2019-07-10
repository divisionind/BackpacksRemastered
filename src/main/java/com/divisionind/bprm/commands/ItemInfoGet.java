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
import com.divisionind.bprm.NBTType;
import com.divisionind.bprm.NMSReflector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class ItemInfoGet extends ACommand {
    @Override
    public String alias() {
        return "item:info:get";
    }

    @Override
    public String desc() {
        return "gets the type and value of the NBT data key specified";
    }

    @Override
    public String usage() {
        return "<key>";
    }

    @Override
    public String permission() {
        return "backpacks.item.info.get";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player p = validatePlayer(sender);
        ItemStack item = p.getInventory().getItemInMainHand();

        if (args.length == 1) {
            respondiu(sender, label);
            return;
        }

        try {
            Object tagCompound = NMSReflector.getNBTTagCompound(NMSReflector.asNMSCopy(item));

            if (NMSReflector.hasNBTKey(tagCompound, args[1])) {
                byte btype = NMSReflector.getKeyInternalTypeId(tagCompound, args[1]);
                NBTType type = NBTType.getByInternalId(btype);

                if (type == null) {
                    respondf(sender, "&cCould not resolve data type for key \"%s\". The internal type id was \"0x%02x or %s\".", args[1], btype, btype);
                    return;
                }

                respondf(sender, "&eData:&7 %s", NMSReflector.getNBT(tagCompound, type, args[1]));
                respondf(sender, "&eData Type:&7 %s", type.name());
            } else {
                respondf(sender, "&cKey \"%s\" not found. See a list of keys with the item:info command.", args[1]);
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}