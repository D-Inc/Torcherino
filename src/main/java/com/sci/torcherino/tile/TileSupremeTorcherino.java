package com.sci.torcherino.tile;

/**
 * Created by ninjawarrior1337 on 4/8/2017.
 */
public class TileSupremeTorcherino extends TileTorcherino
{
    @Override
    protected int speed(final int base) {
        return base * 6561;
    }
}
