package net.pinger.disguise.packet.v1_19_4;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.BiomeManager;
import net.pinger.disguise.Skin;
import net.pinger.disguise.annotation.PacketHandler;
import net.pinger.disguise.data.PlayerDataWrapper;
import net.pinger.disguise.packet.PacketProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@PacketHandler(version = "1.19.4")
public class PacketProviderImpl implements PacketProvider {

    private final Plugin plugin;

    public PacketProviderImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public GameProfile getGameProfile(Player player) {
        return ((CraftPlayer) player).getProfile();
    }

    @Override
    public Skin getProperty(Player player) {
        GameProfile profile = this.getGameProfile(player);
        Collection<Property> textures = profile.getProperties().get("textures");

        // Check if the textures may be empty
        if (textures.isEmpty()) {
            return null;
        }

        Optional<Property> any = textures.stream().filter(property -> property.getValue() != null).findAny();
        return any.map(property -> new Skin(property.getValue(), property.getSignature())).orElse(null);
    }

    @Override
    public void updateProperties(Player player, @Nonnull Skin skin) {
        GameProfile profile = this.getGameProfile(player);

        // Check if the skin isn't equal to the property
        // Note that this shouldn't happen
        if (!(skin.getHandle() instanceof Property)) {
            return;
        }

        // Cast to the property
        Property prop = (Property) skin.getHandle();

        // Clear the properties of this player
        // And add the new ones
        this.clearProperties(player);
        profile.getProperties().put("textures", prop);
    }

    @Override
    public void clearProperties(Player player) {
        this.getGameProfile(player).getProperties().removeAll("textures");
    }

    @Override
    public void sendPacket(Player player, Object... packet) {
        // Get the entity player from the base player
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // Loop through each packet
        // And send it to this player
        for (Object p : packet) {
            entityPlayer.connection.send((Packet<?>) p);
        }
    }

    @Override
    public void sendServerPackets(Player player) {
        // Get the entity player from the base player
        ServerPlayer sp = ((CraftPlayer) player).getHandle();

        // Create the PacketPlayOutRespawn packet
        ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                sp.getCommandSenderWorld().dimensionTypeId(),
                sp.getCommandSenderWorld().dimension(),
                BiomeManager.obfuscateSeed(sp.getLevel().getSeed()),
                sp.gameMode.getGameModeForPlayer(),
                sp.gameMode.getPreviousGameModeForPlayer(),
                false,
                sp.getLevel().isFlat(),
                (byte) 3,
                sp.getLastDeathLocation()
        );

        // Get the name and stuff
        Location l = player.getLocation();

        // Send position
        ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);

        //this.sendPacket(new ClientboundRemoveEntitiesPacket(entityPlayer.getId()));
        this.sendPacket(player, new ClientboundPlayerInfoRemovePacket(Collections.singletonList(sp.getUUID())));

        this.sendPacket(player, respawn);
        PlayerDataWrapper wrapper = new PlayerDataWrapper(player);
        wrapper.applyProperties();

        this.sendPacket(player, ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(sp)));

        // Send the pos packet
        this.sendPacket(player, pos);
        PacketProvider.refreshPlayer(player, this.plugin);
    }
}
