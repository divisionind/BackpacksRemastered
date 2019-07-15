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
import com.divisionind.bprm.BackpackObject;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.nms.NBTType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Split extends ACommand {
    @Override
    public String alias() {
        return "split";
    }

    @Override
    public String desc() {
        return "separates a backpack in your hand from the item it has been combined with";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String permission() {
        return "backpacks.split";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player p = validatePlayer(sender);
        ItemStack inhand = p.getInventory().getItemInMainHand();
        try {
            PotentialBackpackItem backpack = new PotentialBackpackItem(inhand);
            if (backpack.isBackpack() && !inhand.getType().equals(Material.LEATHER_CHESTPLATE)) {

                // copy backpack data to new backpack
                BackpackObject bpo = BackpackObject.getByType(backpack.getType());
                PotentialBackpackItem newBackpack = new PotentialBackpackItem(bpo.getItem());
                newBackpack.setNBT(NBTType.INT, PotentialBackpackItem.FIELD_NAME_TYPE, backpack.getType());
                newBackpack.setNBT(NBTType.BYTE_ARRAY, PotentialBackpackItem.FIELD_NAME_DATA, backpack.getData());

                // remove backpack data from old backpack, including lore
                backpack.removeNBT(PotentialBackpackItem.FIELD_NAME_TYPE);
                backpack.removeNBT(PotentialBackpackItem.FIELD_NAME_DATA);
                ItemStack oldBackpack = backpack.getModifiedItem();
                ItemMeta oldBackpackMeta = oldBackpack.getItemMeta();
                oldBackpackMeta.setLore(new ArrayList<>());
                oldBackpack.setItemMeta(oldBackpackMeta);
                p.getInventory().setItemInMainHand(oldBackpack);

                // add new backpack to inventory
                p.getInventory().addItem(newBackpack.getModifiedItem());
                respond(sender, "&eSplit backpack from what it was combined with.");
            } else respond(sender, "&cThis item is either not a backpack or has not been combined with anything.");
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
