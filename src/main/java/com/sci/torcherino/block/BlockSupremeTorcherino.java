package com.sci.torcherino.block;

import com.sci.torcherino.tile.TileSupremeTorcherino;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by ninjawarrior1337 on 4/8/2017.
 */
public class BlockSupremeTorcherino extends BlockTorcherino
{
    public BlockSupremeTorcherino()
    {
        this.setUnlocalizedName("torcherino.supreme_torcherino");
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int i) {
        return new TileSupremeTorcherino();
    }
}
