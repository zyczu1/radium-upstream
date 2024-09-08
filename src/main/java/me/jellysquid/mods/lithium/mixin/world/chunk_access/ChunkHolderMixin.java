package me.jellysquid.mods.lithium.mixin.world.chunk_access;

import me.jellysquid.mods.lithium.common.world.chunk.ChunkHolderExtended;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin extends AbstractChunkHolder implements ChunkHolderExtended {
    @Unique
    private long lastRequestTime;

    public ChunkHolderMixin(ChunkPos pos) {
        super(pos);
    }

    @Override
    public boolean lithium$updateLastAccessTime(long time) {
        long prev = this.lastRequestTime;
        this.lastRequestTime = time;

        return prev != time;
    }

    @Override
    public WorldChunk getCurrentlyLoading() {
        return this.currentlyLoading;
    }
}
