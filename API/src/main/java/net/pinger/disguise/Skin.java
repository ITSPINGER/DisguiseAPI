package net.pinger.disguise;

import net.pinger.disguise.context.PropertyContext;
import net.pinger.disguise.server.MinecraftServer;
import net.pinger.disguise.skull.SkullManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;

/**
 * This class represents a type which can be used for changing
 * the player's properties through the GameObject type.
 * <p>
 * Each skin has a Base64 encoded value and signature, which is
 * used for the transformation.
 *
 * @since 1.0
 */

public class Skin {

    private final String value;
    private final String signature;

    private final ItemStack skull = new ItemStack(Material.PLAYER_HEAD, (short) 3);

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;

        // Update the meta
        SkullMeta meta = (SkullMeta) this.skull.getItemMeta();
        SkullManager.mutateItemMeta(meta, this);
        this.skull.setItemMeta(meta);
    }

    /**
     * This method returns the encoded
     * value of this skin.
     *
     * @return the value
     */

    public String getValue() {
        return this.value;
    }

    /**
     * This method returns the encoded
     * signature of this skin.
     *
     * @return the signature
     */

    public String getSignature() {
        return this.signature;
    }

    /**
     * Transforms this skin to a skull.
     * <p>
     * This instance is created once the skin has been initialized.
     *
     * @return the skull from this item
     */

    public ItemStack toSkull() {
        return this.skull;
    }

    /**
     * This method returns the property handle
     * for this skin.
     *
     * @return the handle
     */

    @Nonnull
    public Object getHandle() {
        return PropertyContext.createProperty(this);
    }

}
