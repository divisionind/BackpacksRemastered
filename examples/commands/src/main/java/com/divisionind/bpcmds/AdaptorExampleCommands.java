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

package com.divisionind.bpcmds;

import com.divisionind.bprm.ACommand;
import com.divisionind.bprm.Backpacks;
import com.divisionind.bprm.PluginAdaptor;
import com.divisionind.bprm.PluginAdaptorMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@PluginAdaptorMeta(name = "ExampleCommands")
public class AdaptorExampleCommands extends PluginAdaptor {
    @Override
    public void onEnable(Plugin parent) throws Exception {
        Backpacks.getInstance().registerCommands(new CExampleCommand());
        getLogger().info("Registered ExamplePlugin adaptor!");
    }

    private static class CExampleCommand extends ACommand {
        @Override
        public String alias() {
            return "example";
        }

        @Override
        public String desc() {
            return "an example command registered by an example adaptor";
        }

        @Override
        public String usage() {
            return null;
        }

        @Override
        public String permission() {
            return "backpacks.example";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            // need a player instance? Player player = validatePlayer(sender);
            // if the sender is not a player, the command will return and respond accordingly
            respond(sender, "&eHello world!");
        }
    }
}
