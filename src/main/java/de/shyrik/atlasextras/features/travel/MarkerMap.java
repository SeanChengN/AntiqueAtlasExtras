package de.shyrik.atlasextras.features.travel;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.core.Configuration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarkerMap extends WorldSavedData {

    public static final String DATA_ID = AtlasExtras.MODID + ":atlasmarkermap";
    private Map<Integer, Mark> marks = new ConcurrentHashMap<>();

    public MarkerMap() {
        super(DATA_ID);
    }

    public MarkerMap(String s) {
        super(s);
    }

    public static MarkerMap instance(@Nonnull World world) {
        MarkerMap map = (MarkerMap)world.getPerWorldStorage().getOrLoadData(MarkerMap.class, DATA_ID);
        if(map == null) {
            map = new MarkerMap();
            world.getPerWorldStorage().setData(DATA_ID, map);
        }
        return map;
    }


    public void put(int id, BlockPos pos, boolean canJumpTo, boolean canJumpFrom, String modid) {
        marks.put(id, new Mark(id, pos, canJumpTo, canJumpFrom, modid));
        markDirty();
    }

    public void remove(int id) {
        marks.remove(id);
        markDirty();
    }

    public Mark get(int id) {
        if(marks.containsKey(id))
            return marks.get(id);
        return null;
    }

    public Mark getFromPos(@Nonnull BlockPos pos) {
        for(Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            if(entry.getValue().pos.toLong() == pos.toLong())
                return entry.getValue();
        }
        return null;
    }

    public boolean hasOnPos(@Nonnull BlockPos pos) {
        return getFromPos(pos) != null;
    }

    public boolean hasJumpNearby(@Nonnull BlockPos pos) {
        for(Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            if(entry.getValue().canJumpFrom && entry.getValue().pos .getDistance(pos.getX(), pos.getY(), pos.getZ()) <= Configuration.COMPAT.distanceToMarker)
                return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList nbtList = nbt.getTagList("marks", 10);
        for (int i = 0; i < nbtList.tagCount(); i++) {
            NBTTagCompound tag = nbtList.getCompoundTagAt(i);
            int id = tag.getInteger("id");

            marks.put(id, new Mark(
                    id,
                    new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")),
                    tag.getBoolean("jumpto"), tag.getBoolean("jumpfrom"),
                    tag.getString("modid")
            ));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList nbtList = new NBTTagList();
        for (Map.Entry<Integer, Mark> entry : marks.entrySet()) {
            Mark mark = entry.getValue();

            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("id", mark.id);
            nbt.setInteger("x", mark.pos.getX());
            nbt.setInteger("y", mark.pos.getY());
            nbt.setInteger("z", mark.pos.getZ());
            nbt.setBoolean("jumpto", mark.canJumpTo);
            nbt.setBoolean("jumpfrom", mark.canJumpFrom);
            nbt.setString("modid", mark.modid);

            nbtList.appendTag(nbt);
        }
        compound.setTag("marks", nbtList);
        return compound;
    }

    public class Mark {
        public int id;
        public BlockPos pos;
        public boolean canJumpTo;
        public boolean canJumpFrom;
        public String modid;

        public Mark(int id, BlockPos pos, boolean canJumpTo, boolean canJumpFrom, String modid) {
            this.id = id;
            this.pos = pos;
            this.canJumpTo = canJumpTo;
            this.canJumpFrom = canJumpFrom;
            this.modid = modid;
        }
    }
}