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
        plugin.getCommand("pvptoggle").setExecutor(this);

        try
        {
            if (!playerRegFile.exists())
                playerRegFile.createNewFile();
        }
        catch(IOException e)
        {
            System.err.println(e);
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
            System.err.println(e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player)
        {
            if(command.getName() == "pvptoggle")
            {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();

                LocalDateTime time = (LocalDateTime) playerRegister.get("players." + uuid.toString());

                if(time == null || !(boolean) playerRegister.get("players." + uuid.toString() + ".active"))
                {
                    playerRegister.set("players." + uuid.toString() + ".time", LocalDateTime.now());
                    playerRegister.set("players." + uuid.toString() + ".active", true);
                }
                else
                {
                    if(time.isAfter(LocalDateTime.now().minusSeconds(cooldown)))
                    {
                        playerRegister.set("players." + uuid.toString() + ".time", null);
                        playerRegister.set("players." + uuid.toString() + ".active", false);
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
                eventInfo.setCancelled(true);
        }
    }
}
