package me.chaz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.time.LocalDateTime;

public class PVPToggle implements CommandExecutor
{
    private final Main plugin;

    private FileConfiguration playerRegister;
    private File playerRegFile = new File("PlayerRegister.yml");
    private int cooldown;

    public PVPToggle(Main plugin)
    {
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

        if(playerRegister.get("cooldown in seconds") == null)
            playerRegister.set("cooldown in seconds", 900);

        cooldown = (int) playerRegister.get("cooldown in seconds");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player)
        {
            if(command.getName() == "pvptoggle")
            {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();

                LocalDateTime time = (LocalDateTime) playerRegister.get(uuid.toString());

                if(time != null)
                {
                    playerRegister.set(uuid.toString(), LocalDateTime.now());
                }
                else
                {

                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent ede)
    {

    }
}
