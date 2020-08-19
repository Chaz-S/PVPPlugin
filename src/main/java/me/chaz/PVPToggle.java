package me.chaz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PVPToggle implements CommandExecutor
{
    private final Main plugin;

    public PVPToggle(Main plugin)
    {
        this.plugin = plugin;
        plugin.getCommand("pvptoggle").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @EventHandler
    public void onHit(EntityDamageEvent ede)
    {

    }
}
