package org.cyclops.integrateddynamics.core.network;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.core.tileentity.TileCableConnectableInventory;

import java.util.List;

/**
 * Network element for tile entities.
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class TileNetworkElement<T extends TileCableConnectableInventory> extends ConsumingNetworkElementBase<IPartNetwork>  {

    private final DimPos pos;

    protected abstract Class<T> getTileClass();

    protected T getTile() {
        return TileHelpers.getSafeTile(getPos().getWorld(), getPos().getBlockPos(), getTileClass());
    }

    @Override
    public void addDrops(List<ItemStack> itemStacks) {
        T tile = getTile();
        if(tile != null) {
            InventoryHelper.dropInventoryItems(getPos().getWorld(), getPos().getBlockPos(), tile.getInventory());
        }
    }

    @Override
    public int compareTo(INetworkElement o) {
        if(o instanceof TileNetworkElement) {
            return getPos().compareTo(((TileNetworkElement) o).getPos());
        }
        return Integer.compare(hashCode(), o.hashCode());
    }
}
