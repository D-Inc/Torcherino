package com.sci.torcherino.block;

import com.sci.torcherino.tile.TileTripleCompressedTorcherino;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by ninjawarrior1337 on 4/8/2017.
 */
public class BlockTripleCompressedTorcherino extends BlockTorcherino
{
    public BlockTripleCompressedTorcherino()
    {
        this.setUnlocalizedName("torcherino.triple_compressed_torcherino");
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int i) {
        return new TileTripleCompressedTorcherino();
    }
}
