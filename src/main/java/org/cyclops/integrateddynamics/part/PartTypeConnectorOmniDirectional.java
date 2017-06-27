package org.cyclops.integrateddynamics.part;

import com.google.common.collect.Sets;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartRenderPosition;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.path.IPathElement;
import org.cyclops.integrateddynamics.capability.path.PathElementConfig;
import org.cyclops.integrateddynamics.core.block.IgnoredBlock;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.helper.L10NValues;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An omnidirectional wireless connector part that can connect to
 * all other monodirectional connectors of the same group anywhere in any dimension.
 * @author rubensworks
 */
public class PartTypeConnectorOmniDirectional extends PartTypeConnector<PartTypeConnectorOmniDirectional, PartTypeConnectorOmniDirectional.State> {

    public static LoadedGroups LOADED_GROUPS = new LoadedGroups();
    private static String NBT_KEY_ID = "omnidir-group-key";

    public PartTypeConnectorOmniDirectional(String name) {
        super(name, new PartRenderPosition(0.25F, 0.3125F, 0.625F, 0.625F));
    }

    @Override
    public int getConsumptionRate(State state) {
        return 128;
    }

    @Override
    public PartTypeConnectorOmniDirectional.State constructDefaultState() {
        return new PartTypeConnectorOmniDirectional.State();
    }

    @Override
    public Class<? super PartTypeConnectorOmniDirectional> getPartTypeClass() {
        return PartTypeConnectorOmniDirectional.class;
    }

    @Override
    public ItemStack getItemStack(State state) {
        ItemStack itemStack = super.getItemStack(state);
        if (state.hasConnectorId()) {
            NBTTagCompound tag = ItemStackHelpers.getSafeTagCompound(itemStack);
            tag.setInteger(NBT_KEY_ID, state.getGroupId());
        }
        return itemStack;
    }

    @Override
    public State getState(ItemStack itemStack) {
        State state = super.getState(itemStack);
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag != null && tag.hasKey(NBT_KEY_ID, MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            state.setGroupId(tag.getInteger(NBT_KEY_ID));
        } else {
            state.setGroupId(PartTypeConnectorOmniDirectional.generateGroupId());
        }
        return state;
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addPosition(network, state, target.getCenter());
    }

    @Override
    public void onPostRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onPostRemoved(network, partNetwork, target, state);
        removePosition(network, state, target.getCenter());
    }

    protected void addPosition(INetwork network, State state, PartPos pos) {
        if (network.isInitialized()
                && !PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions()
                && !state.isAddedToGroup()) {
            state.setAddedToGroup(true);
            PartTypeConnectorOmniDirectional.LOADED_GROUPS.addPosition(state.getGroupId(), pos);
        }
    }

    protected void removePosition(INetwork network, State state, PartPos pos) {
        if (network.isInitialized()
                && !PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions()
                && state.isAddedToGroup()) {
            if (state.hasConnectorId()) {
                state.setAddedToGroup(false);
                PartTypeConnectorOmniDirectional.LOADED_GROUPS.removePosition(state.getGroupId(), pos);
            }
        }
    }

    public static int generateGroupId() {
        //return IntegratedDynamics.globalCounters.getNext("omnidir-connectors");
        return 100; // TODO: change when implemented recipes
    }

    @Override
    public void loadTooltip(State state, List<String> lines) {
        super.loadTooltip(state, lines);
        lines.add(L10NHelpers.localize(L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP, state.getGroupId()));
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {
        super.loadTooltip(itemStack, lines);
        if (itemStack.hasTagCompound()) {
            lines.add(L10NHelpers.localize(L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP,
                    itemStack.getTagCompound().getInteger(NBT_KEY_ID)));
        }
    }

    protected IgnoredBlockStatus.Status getStatus(PartTypeConnectorOmniDirectional.State state) {
        return state != null && state.hasConnectorId()
                ? IgnoredBlockStatus.Status.ACTIVE : IgnoredBlockStatus.Status.INACTIVE;
    }

    @Override
    public IBlockState getBlockState(IPartContainer partContainer, EnumFacing side) {
        IgnoredBlockStatus.Status status = getStatus(partContainer != null
                ? (PartTypeConnectorOmniDirectional.State) partContainer.getPartState(side) : null);
        return super.getBlockState(partContainer, side).withProperty(IgnoredBlock.FACING, side).
                withProperty(IgnoredBlockStatus.STATUS, status);
    }

    public static class State extends PartTypeConnector.State<PartTypeConnectorOmniDirectional> {

        private int groupId = -1;
        private boolean addedToGroup = false;

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setInteger(NBT_KEY_ID, groupId);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            this.groupId = tag.getInteger(NBT_KEY_ID);
        }

        @Override
        public Set<IPathElement> getReachableElements() {
            if (hasConnectorId()) {
                Set<IPathElement> pathElements = Sets.newTreeSet();
                for (PartPos pos : PartTypeConnectorOmniDirectional.LOADED_GROUPS.getPositions(getGroupId())) {
                    if (!pos.equals(this.getPartPos())) {
                        IPathElement pathElement = TileHelpers.getCapability(pos.getPos(), pos.getSide(),
                                PathElementConfig.CAPABILITY);
                        if (pathElement != null) {
                            pathElements.add(pathElement);
                        }
                    }
                }
                return pathElements;
            }
            return Collections.emptySet();
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
            sendUpdate();
        }

        public boolean hasConnectorId() {
            return this.groupId >= 0;
        }

        public boolean isAddedToGroup() {
            return addedToGroup;
        }

        public void setAddedToGroup(boolean addedToGroup) {
            this.addedToGroup = addedToGroup;
        }
    }

    public static class LoadedGroups {

        private TIntObjectMap<Set<PartPos>> groupPositions = new TIntObjectHashMap<>();
        private boolean modifyingPositions = false;

        public void onStartedEvent(FMLServerStartedEvent event) {
            // Reset to avoid ghost-groups on world-change.
            groupPositions.clear();
        }

        public Set<PartPos> getPositions(int group) {
            Set<PartPos> positions = groupPositions.get(group);
            return positions != null ? positions : Collections.<PartPos>emptySet();
        }

        protected void initNetworkGroup(Set<PartPos> positions) {
            for (PartPos position : positions) {
                if (position.getPos().isLoaded()) {
                    NetworkHelpers.initNetwork(position.getPos().getWorld(), position.getPos().getBlockPos());
                }
            }
        }

        public void addPosition(int group, PartPos pos) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.add(pos);

            modifyingPositions = true;
            initNetworkGroup(positions);
            modifyingPositions = false;
        }

        public void removePosition(int group, PartPos pos) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.remove(pos);

            modifyingPositions = true;
            initNetworkGroup(positions);
            if (pos.getPos().isLoaded()) {
                NetworkHelpers.initNetwork(pos.getPos().getWorld(), pos.getPos().getBlockPos());
            }
            modifyingPositions = false;
        }

        public boolean isModifyingPositions() {
            return modifyingPositions;
        }
    }
}