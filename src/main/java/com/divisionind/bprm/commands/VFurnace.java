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
import com.divisionind.bprm.VirtualFurnace;
import com.divisionind.bprm.location.ItemStackPointer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.divisionind.bprm.events.BackpackFurnaceTickEvent.VIRTUAL_FURNACES;

public class VFurnace extends ACommand {

    public static final int RESULTS_PER_PAGE = 4;

    @Override
    public String alias() {
        return "vfurnace";
    }

    @Override
    public String desc() {
        return "lists virtual furnaces and their assumed backpack locations";
    }

    @Override
    public String usage() {
        return "<page>";
    }

    @Override
    public String permission() {
        return "backpacks.vfurnace";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        int page;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                respond(sender, "&cError: This argument must be an integer.");
                return;
            }
        } else page = 1;

        int numberOfPages = (VIRTUAL_FURNACES.size() / RESULTS_PER_PAGE) + 1;

        if (page > numberOfPages) {
            respondf(sender, "&cError: This help page does not exist. The maximum help page is %s.", numberOfPages);
            return;
        }

        respondf(sender, "&bPage (&e%s &b/ &e%s&b)", page, numberOfPages);

        for (Map.Entry<UUID, VirtualFurnace> entry : getOnPage(page).entrySet()) {
            ItemStackPointer itemStackPointer = entry.getValue().getItemLocation();
            respondnf(sender, "&eID:&7 %s &r&e\n| Location: &7[%s]&r", entry.getKey().toString(), itemStackPointer == null ? "unknown" : itemStackPointer.getSurfaceLocation().toString());
        }
    }
    public static Map<UUID, VirtualFurnace> getOnPage(int page) {
        Map<UUID, VirtualFurnace> newMap = new HashMap<>();

        int startCmd = (page - 1) * RESULTS_PER_PAGE;
        for (int i = startCmd;(i<VIRTUAL_FURNACES.size() && (i - startCmd) < RESULTS_PER_PAGE);i++) {

            int slot = 0;
            for (Map.Entry<UUID, VirtualFurnace> entry : VIRTUAL_FURNACES.entrySet()) {
                if (slot++ == i) {
                    newMap.put(entry.getKey(), entry.getValue());
                    break;
                }
            }
        }

        return newMap;
    }
}
