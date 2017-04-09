package com.sci.torcherino.tile;

/**
 * Created by ninjawarrior1337 on 4/8/2017.
 */
public class TileTripleCompressedTorcherino extends TileTorcherino
{
    @Override
    protected int speed(final int base) {
        return base * 729;
    }
}
