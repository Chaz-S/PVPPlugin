package me.chaz;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        new PVPToggle(this);
    }

    @Override
    public void onDisable()
    {

    }
}
