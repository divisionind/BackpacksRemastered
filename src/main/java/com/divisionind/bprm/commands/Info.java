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
import com.divisionind.bprm.Backpacks;
import org.bukkit.command.CommandSender;

public class Info extends ACommand {
    @Override
    public String alias() {
        return "info";
    }

    @Override
    public String desc() {
        return "displays info about the plugin";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String permission() {
        return "backpacks.info";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        respond(sender, "&e&lInfo");
        respondn(sender, "&7Created by drew6017 (Andrew Howard) as a remake of his original plugin with more features and optimizations.");
        respondnf(sender, "&eVersion: &a%s", Backpacks.VERSION);
        respondnf(sender, "&eGit Commit: &a%s", Backpacks.GIT_HASH);
        respondnf(sender, "&eGit Build: &a%s", Backpacks.GIT_NUM);
        respondn(sender,  "&eDownload Page: &ahttps://dev.bukkit.org/projects/backpack-item");
    }
}