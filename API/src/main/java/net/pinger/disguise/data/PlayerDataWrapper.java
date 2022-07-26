package net.pinger.disguise.data;

import net.pinger.disguise.server.MinecraftServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PlayerDataWrapper {

    private final Player player;

    private final GameMode gameMode;
    private final boolean allowFlight;
    private final boolean flying;
    private final Location location;
    private final double maxHealth;
    private final double health;
    private final int level;
    private final float xp;

    public PlayerDataWrapper(Player player) {
        this.player = player;

        // Update other fields
        this.gameMode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.flying = player.isFlying();
        this.location = player.getLocation();
        this.level = player.getLevel();
        this.xp = player.getExp();
        this.maxHealth = MinecraftServer.atLeast("1.9") ? player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : player.getMaxHealth();
        this.health = player.getHealth();
    }

    /**
     * This method applies the saved properties to the
     * given player.
     */

    public void applyProperties() {
        if (this.player == null)
            return;

        // Update the properties here
        this.player.setGameMode(this.gameMode);
        this.player.setAllowFlight(this.allowFlight);
        this.player.setFlying(this.flying);
        this.player.teleport(this.location);
        this.player.updateInventory();
        this.player.setMaxHealth(this.maxHealth);
        this.player.setHealth(this.health);
        this.player.setLevel(this.level);
        this.player.setExp(this.xp);
    }
}
