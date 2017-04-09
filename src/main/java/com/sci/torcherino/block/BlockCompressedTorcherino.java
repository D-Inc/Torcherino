package com.sci.torcherino.block;

import com.sci.torcherino.Torcherino;
import com.sci.torcherino.tile.TileCompressedTorcherino;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class BlockCompressedTorcherino extends BlockTorcherino {
    public BlockCompressedTorcherino() {
        this.setUnlocalizedName("torcherino.compressed_torcherino");
        setCreativeTab(Torcherino.taberino);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int i) {
        return new TileCompressedTorcherino();
    }
}