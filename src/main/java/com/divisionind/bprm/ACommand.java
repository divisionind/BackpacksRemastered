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

package com.divisionind.bprm;

import com.divisionind.bprm.exceptions.IncorrectUsageException;
import com.divisionind.bprm.exceptions.PlayerRequiredException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ACommand {

    public abstract String alias();

    public abstract String desc();

    public abstract String usage();

    public abstract String permission();

    public abstract void execute(CommandSender sender, String label, String[] args);

    // TODO ensure that commands are processed on a single thread before doing this
//    protected CommandSender currentSender;
//
//    protected void respond(String msg) {
//        respond(currentSender, msg);
//    }

    // recursively do these as to always have tab complete available
    // if null, displays online players | if empty list, displays nothing
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    protected boolean matchesAlias(String a) {
        return alias().equals(a);
    }

    public boolean hasPerm(CommandSender sender) {
        return sender.hasPermission(permission());
    }

    // incorrect usage
    public void respondiu(CommandSender sender, String label) {
        respondf(sender, "&cIncorrect usage. Correct usage: /%s %s %s", label, alias(), usage());
    }

    public static void respond(CommandSender sender, String msg) {
        msg = Backpacks.PREFIX + Backpacks.translate(msg);
        respondn(sender, msg);
    }

    public static void respondf(CommandSender sender, String msg, Object... objects) {
        respond(sender, String.format(msg, objects));
    }

    public static void respondn(CommandSender sender, String msg) {
        msg = Backpacks.translate(msg);
        sender.sendMessage(sender instanceof ConsoleCommandSender ? ChatColor.stripColor(msg) : msg);
    }

    public static void respondnf(CommandSender sender, String msg, Object... objects) {
        respondn(sender, String.format(msg, objects));
    }

    public static void respondnop(CommandSender sender) {
        respond(sender, "&cThis operation is not supported yet.");
    }

    /**
     * Validates that the command sender is a player and if so, returns player instance.
     *
     * @param sender entity in question
     * @return player instance
     */
    public static Player validatePlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player)sender;
        } else throw new PlayerRequiredException();
    }

    /**
     * Validates that the arguments length is as specified.
     *
     * @param args args in question
     * @param length length the args should be
     */
    public static void validateArgsLength(String[] args, int length) {
        if (args.length != length) throw new IncorrectUsageException();
    }

    /**
     * Calls the command only if the sender has permission.
     *
     * @param sender
     * @param label
     * @param args
     */
    public void call(CommandSender sender, String label, String[] args) {
        if (hasPerm(sender)) {
            try {
                //this.currentSender = sender;
                execute(sender, label, args);
            } catch (PlayerRequiredException e) {
                respond(sender, "&cYou must be a player to use this command.");
            } catch (IncorrectUsageException e) {
                respondiu(sender, label);
            }
        } else {
            respond(sender, String.format("&cYou do not have permission to use this command. This command requires the permission \"%s\".", permission()));
        }
    }
}
