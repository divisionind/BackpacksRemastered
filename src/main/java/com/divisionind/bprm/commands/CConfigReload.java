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
import com.divisionind.bprm.Backpacks;
import org.bukkit.command.CommandSender;

public class CConfigReload extends ACommand {
    @Override
    public String alias() {
        return "config:reload";
    }

    @Override
    public String desc() {
        return "reloads the yml configuration";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String permission() {
        return "backpacks.config.reload";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        respond(sender,
                "&cWARNING: This command is not fully supported. Only use this for testing new configurations and ensure to reload the server once you are done.");
        respond(sender, "&eReloading the configuration...");
        Backpacks.getInstance().reloadConfig();
        Backpacks.getInstance().setupFromConfig();
        respond(sender, "&eConfiguration has been reloaded.");
    }
}