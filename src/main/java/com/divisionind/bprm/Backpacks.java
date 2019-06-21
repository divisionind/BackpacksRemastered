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

package com.divisionind.bprm;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backpacks extends JavaPlugin {

    public static final String PREFIX = translate("&9Backpacks &7&l>>&r ");
    public static final String VERSION = "@DivisionVersion@";
    public static final String GIT_HASH = "@DivisionGitHash@";
    public static final String GIT_NUM = "@DivisionGitComm@";

    private static List<ACommand> commands;

    @Override
    public void onEnable() {
        commands = new ArrayList<>();
        registerCMDS(new Commands.Help(),
                new Commands.ConfigReload());

        getLogger().info(String.format("BackpacksRemastered v%s (git: %s) has been enabled!", VERSION, GIT_HASH));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("BackpacksRemastered v%s (git: %s) has been disabled.", VERSION, GIT_HASH));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("backpacks")) {
            if (args.length > 0) {
                String subcmd = args[0].toLowerCase();
                for (ACommand command : commands) {
                    if (command.matchesAlias(subcmd)) {
                        command.call(sender, label, args);
                        return true;
                    }
                }

                // no command found by sub, attempts to parse as int for help pages
                try {
                    int hpage = Integer.parseInt(subcmd);
                    new Commands.Help().call(sender, label, new String[] {"help", Integer.toString(hpage)});
                } catch (NumberFormatException e) {
                    // not an int, idk wtf they were doing then
                    ACommand.respondf(sender, "&cError: Command \"%s\" not found.", subcmd);
                }
            } else new Commands.Help().call(sender, label, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // see https://hub.spigotmc.org/javadocs/spigot/org/bukkit/plugin/java/JavaPlugin.html#onTabComplete-org.bukkit.command.CommandSender-org.bukkit.command.Command-java.lang.String-java.lang.String:A-

        List<String> entries = new ArrayList<>();

        if (args.length > 1) {
            for (ACommand cmd : commands) {
                if (cmd.matchesAlias(args[0])) {
                    if (!cmd.hasPerm(sender)) break;
                    entries = cmd.tabComplete(sender, command, alias, args);
                    break;
                }
            }
        } else
        if (args.length == 1) {
            for (ACommand cmd : commands) {
                if (!cmd.hasPerm(sender)) continue;
                String ali = cmd.alias();
                if (ali.startsWith(args[0])) entries.add(ali);
            }
        } else {
            for (ACommand cmd : commands) {
                if (!cmd.hasPerm(sender)) continue;
                entries.add(cmd.alias());
            }
        }

        return entries;
    }

    public static void registerCMDS(ACommand... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    public static List<ACommand> getCommands() {
        return commands;
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }
}
