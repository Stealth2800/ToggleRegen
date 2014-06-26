package com.stealthyone.mcbc.toggleregen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ToggleRegen extends JavaPlugin implements Listener {

    private Set<UUID> disabledRegen = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ToggleRegen v" + getDescription().getVersion() + " by Stealth2800 ENABLED.");
    }

    @Override
    public void onDisable() {
        disabledRegen.clear();
        getLogger().info("ToggleRegen v" + getDescription().getVersion() + " by Stealth2800 DISABLED.");
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player && e.getRegainReason() == RegainReason.SATIATED && disabledRegen.contains(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        playerLeave(e.getPlayer());
    }

    private void playerLeave(Player player) {
        disabledRegen.remove(player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("toggleregen.toggle")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must specify a player.");
            return true;
        }

        String name = args[0];
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player: " + ChatColor.GOLD + name);
            return true;
        }
        UUID uuid = player.getUniqueId();

        boolean isDisabled = disabledRegen.contains(uuid);
        if (isDisabled) {
            disabledRegen.remove(uuid);
        } else {
            disabledRegen.add(uuid);
        }
        sender.sendMessage((isDisabled ? (ChatColor.GREEN + "Enabled") : (ChatColor.RED + "Disabled"))
                + ChatColor.BLUE + " natural regeneration for " + ChatColor.GOLD + player.getName() + ChatColor.BLUE + ".");
        return true;
    }

}