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
import com.divisionind.bprm.BackpackSerialization;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.backpacks.BPCombined;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CSplit extends ACommand {
    @Override
    public String alias() {
        return "split";
    }

    @Override
    public String desc() {
        return "separates a backpack in your hand from the item it has been combined with or pulls backpack " +
                "out of a combined backpack";
    }

    @Override
    public String usage() {
        return "<none:slot>";
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
            if (backpack.isBackpack()) {

                // if is a base backpack (not combined with anything)
                if (inhand.getType().equals(Material.LEATHER_CHESTPLATE)) {

                    // if a combined backpack
                    if (backpack.getType() == BackpackObject.COMBINED.getTypeId()) {
                        validateArgsLength(args, 2);

                        // get slot from cmd
                        int slot;
                        try {
                            slot = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            respondf(sender, "&cThe argument \"%s\" is not a valid integer.", args[1]);
                            return;
                        }

                        // ensure no out of bounds exception
                        Inventory combinedInv = BackpackSerialization.fromByteArrayInventory(backpack.getData());
                        if (slot >= combinedInv.getSize()) {
                            respondf(sender, "&cThe supplied slot number is too large. The max is %s.", slot);
                            return;
                        }

                        // check if item specified is null
                        ItemStack item = combinedInv.getItem(slot);
                        if (item == null) {
                            respondf(sender, "&cA backpack was not found in slot %s of the combined backpack.", slot);
                            return;
                        }

                        // null item before giving it back to favor the deletion of the item rather than duplication
                        combinedInv.setItem(slot, null);
                        backpack.setData(BackpackSerialization.toByteArrayInventory(combinedInv, BPCombined.NAME));
                        p.getInventory().setItemInMainHand(backpack.getModifiedItem());

                        safeAddItem(p, item);
                        respond(sender, "&eRemoved backpack from combined backpack.");
                        return;
                    }

                    // if not
                    respond(sender, "&cThis backpack is not combined with anything.");
                    return;
                }

                // copy backpack data to new backpack
                BackpackObject bpo = backpack.getTypeObject();

                // will cause null pointer if backpack does not exist in this version
                PotentialBackpackItem newBackpack = new PotentialBackpackItem(bpo.getItem());
                newBackpack.setType(backpack.getType());
                newBackpack.setData(backpack.getData());

                // remove backpack data from old backpack, including lore
                backpack.removeNBT(PotentialBackpackItem.FIELD_NAME_TYPE);
                backpack.removeNBT(PotentialBackpackItem.FIELD_NAME_DATA);
                ItemStack oldBackpack = backpack.getModifiedItem();
                ItemMeta oldBackpackMeta = oldBackpack.getItemMeta();
                oldBackpackMeta.setLore(new ArrayList<>());
                oldBackpack.setItemMeta(oldBackpackMeta);
                p.getInventory().setItemInMainHand(oldBackpack);

                // add new backpack to inventory
                safeAddItem(p, newBackpack.getModifiedItem());
                respond(sender, "&eSplit backpack from what it was combined with.");
            } else respond(sender, "&cThis item is not a backpack.");
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | IOException
                | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void safeAddItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() < 0) {
            player.getLocation().getWorld()
                    .dropItem(player.getLocation(), item)
                    .setVelocity(new Vector(0, 0, 0));
        } else {
            player.getInventory().addItem(item);
        }
    }
}
