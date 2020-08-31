package me.chaz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

public class PVPToggle implements CommandExecutor
{
    private final Main plugin;

    private YamlConfiguration playerRegister;
    private File playerRegFile = new File("PlayerRegister.yml");
    private int cooldown;

    public PVPToggle(Main plugin)
    {
        //
        this.plugin = plugin;

        try
        {
            plugin.getCommand("pvptoggle").setExecutor(this);
        }
        catch(Exception e){}

        try
        {
            if (!playerRegFile.exists())
                playerRegFile.createNewFile();
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }

        playerRegister = YamlConfiguration.loadConfiguration(playerRegFile);

//        if(!playerRegister.isConfigurationSection("general_config"))
//            playerRegister.createSection("general_config");

        if(!playerRegister.isSet("general_config.cooldown_in_seconds"))
            playerRegister.set("general_config.cooldown_in_seconds", 900);

        cooldown = (int) playerRegister.get("general_config.cooldown_in_seconds");

//        if(!playerRegister.isConfigurationSection("players"))
//            playerRegister.createSection("players");

        try
        {
            playerRegister.save(playerRegFile);
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player)
        {
            if(command.getName().contentEquals("pvptoggle"))
            {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();

                LocalDateTime time;

                try
                {
                    time = LocalDateTime.parse(playerRegister.get("players." + uuid.toString() + ".time").toString());
                }
                catch(NullPointerException e)
                {
                    time = null;
                }

                if(time != null)
                    sender.sendMessage("time: " + time.toString());

                if(time == null || playerRegister.get("players." + uuid.toString() + ".active").toString().contentEquals("false"))
                {
                    playerRegister.set("players." + uuid.toString() + ".time", LocalDateTime.now().toString());
                    playerRegister.set("players." + uuid.toString() + ".active", true);

                    try
                    {
                        playerRegister.save(playerRegFile);
                    }
                    catch(IOException e)
                    {
                        System.err.println(e.getMessage());
                    }

                    sender.sendMessage("[PVP on.]");
                }
                else
                {
                    if(player.hasPermission("pvptoggle.bypasscooldown") || time.isBefore(LocalDateTime.now().plusSeconds(cooldown)))
                    {
                        playerRegister.set("players." + uuid.toString() + ".time", null);
                        playerRegister.set("players." + uuid.toString() + ".active", false);

                        try
                        {
                            playerRegister.save(playerRegFile);
                        }
                        catch(IOException e)
                        {
                            System.err.println(e.getMessage());
                        }

                        sender.sendMessage("[PVP off.]");
                    }
                    else
                    {
                        long hours = time.until(LocalDateTime.now(), ChronoUnit.HOURS);
                        long mins = time.until(LocalDateTime.now(), ChronoUnit.MINUTES) % 59;
                        long secs = time.until(LocalDateTime.now(), ChronoUnit.SECONDS) % 59;

                        sender.sendMessage("[You can't disable PVP yet, cooldown: " + (hours != 0 ? hours + " hours, " : "") + (mins != 0 ? mins  + " minutes, " : "") + secs + " seconds]");
                    }
                }
            }
        }

        return true;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent eventInfo)
    {
        if(eventInfo.getDamager() instanceof Player && eventInfo.getEntity() instanceof Player)
        {
            UUID attackedPlyr = eventInfo.getEntity().getUniqueId();

            if(playerRegister.contains("players." + attackedPlyr.toString() + ".active"))
                if(playerRegister.get("players." + attackedPlyr.toString() + ".active").toString().contentEquals("false"))
                    eventInfo.setCancelled(true);
        }
    }
}
