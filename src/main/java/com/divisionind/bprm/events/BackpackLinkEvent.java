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

package com.divisionind.bprm.events;

import com.divisionind.bprm.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BackpackLinkEvent implements Listener {
    @EventHandler
    public void onLinkBackpack(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

            ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand();

            try {
                PotentialBackpackItem backpack = new PotentialBackpackItem(inhand);
                if (backpack.isBackpack()) {

                    int type = backpack.getType();
                    if (type == BackpackObject.LINKED.getTypeId()) {

                        // the player is left clicking a block with a linked backpack in hand, this is lookin good
                        Block block = e.getClickedBlock();
                        if (block == null)
                            return;

                        if (block.getState() instanceof Container) {
                            // there is no centralized/clean way of checking for container access that I can think of
                            // so, im going to have to do it on a per-plugin basis
                            // check with the plugin specific adaptor ability to see if the player has access
                            //   to the container
                            AdaptorAbility hasContainerAccess = Backpacks.getAdaptorManager()
                                    .getAbility("hasAccessToContainer");
                            if (hasContainerAccess != null
                                    && !hasContainerAccess.call(boolean.class, e.getPlayer(), block.getLocation())) {
                                ACommand.respond(e.getPlayer(), "&cAccess to this container was denied.");
                                return;
                            }

                            backpack.setData(block.getLocation());
                            ItemStack newBackpack = backpack.getModifiedItem();
                            ItemMeta meta = newBackpack.getItemMeta();
                            List<String> newLore = new ArrayList<>(BackpackObject.LINKED.getLore());
                            newLore.add("");
                            newLore.add(Backpacks.translate("&aLinked"));
                            meta.setLore(newLore);
                            newBackpack.setItemMeta(meta);
                            e.getPlayer().getInventory().setItemInMainHand(newBackpack);
                            ACommand.respond(e.getPlayer(),
                                    "&eYou feel a strong connection form between the backpack and the container.");
                        }
                    }
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IOException
                    | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
    }
}
