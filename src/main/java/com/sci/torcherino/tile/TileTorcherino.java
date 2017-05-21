package com.sci.torcherino.tile;

import cofh.api.energy.IEnergyHandler;
import com.sci.torcherino.Torcherino;
import com.sci.torcherino.TorcherinoRegistry;
import com.sci.torcherino.lib.Props;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.Random;

/**
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = Props.COFH_CORE)
public class TileTorcherino extends TileEntity implements IEnergyHandler
{
    private static final String[] MODES = new String[]{"Radius: +.5, Area: 1x3x1", "Radius: +1, Area: 3x3x3", "Radius: +2, Area: 5x3x5", "Radius: +3, Area: 7x3x7", "Radius: +4, Area: 9x3x9", "Stopped"};
    private static final int SPEEDS = 4;

    private static final int MAX_ENERGY_STORED = 1024;

    private int redstoneFlux;

    private boolean requiredRedstoneState;
    private boolean poweredByRedstone;
    private byte speed;
    private byte mode;
    private byte cachedMode;
    private Random rand;

    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public TileTorcherino()
    {
        this(false);
    }

    public TileTorcherino(final boolean requiredRedstoneState)
    {
        this.requiredRedstoneState = requiredRedstoneState;
        this.cachedMode = 6;
        this.rand = new Random();
    }

    protected int speed(final int base)
    {
        return base;
    }

    @Override
    public void updateEntity()
    {
        if (this.worldObj.isRemote)
            return;

        if (this.poweredByRedstone != this.requiredRedstoneState || this.mode == 5 || this.speed == 0)
            return;

        if (Torcherino.useRF && this.redstoneFlux <= 1)
            return;

        this.updateCachedModeIfNeeded();
        this.tickNeighbors();
    }

    private void updateCachedModeIfNeeded()
    {
        if (this.cachedMode != this.mode)
        {
            this.xMin = this.xCoord - this.mode;
            this.yMin = this.yCoord - 1;
            this.zMin = this.zCoord - this.mode;
            this.xMax = this.xCoord + this.mode;
            this.yMax = this.yCoord + 1;
            this.zMax = this.zCoord + this.mode;
            this.cachedMode = this.mode;
        }
    }

    private void tickNeighbors()
    {
        for (int x = this.xMin; x <= this.xMax; x++)
        {
            for (int y = this.yMin; y <= this.yMax; y++)
            {
                for (int z = this.zMin; z <= this.zMax; z++)
                {
                    this.tickBlock(x, y, z);
                }
            }
        }
    }

    private void tickBlock(final int x, final int y, final int z)
    {
        final Block block = this.worldObj.getBlock(x, y, z);

        if (block == null)
            return;

        if (TorcherinoRegistry.isBlockBlacklisted(block))
            return;

        if (block instanceof BlockFluidBase)
            return;

        if (block.getTickRandomly())
        {
            for (int i = 0; i < this.speed; i++)
            {
                if (Torcherino.useRF)
                {
                    if (this.useEnergy(1))
                        block.updateTick(this.worldObj, x, y, z, this.rand);
                }
                else
                {
                    block.updateTick(this.worldObj, x, y, z, this.rand);
                }
            }
        }

        if (block.hasTileEntity(this.worldObj.getBlockMetadata(x, y, z)))
        {
            final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
            if (tile != null && !tile.isInvalid())
            {
                if (TorcherinoRegistry.isTileBlacklisted(tile.getClass()))
                    return;

                for (int i = 0; i < this.speed(this.speed); i++)
                {
                    if (tile.isInvalid())
                        break;

                    if (Torcherino.useRF)
                    {
                        if (this.useEnergy(1))
                            tile.updateEntity();
                    }
                    else
                    {
                        tile.updateEntity();
                    }
                }
            }
        }
    }

    private boolean useEnergy(final int amt)
    {
        if (this.redstoneFlux >= amt)
        {
            this.redstoneFlux -= amt;
            return true;
        }

        return false;
    }

    public void setPoweredByRedstone(final boolean poweredByRedstone)
    {
        this.poweredByRedstone = poweredByRedstone;
    }

    public void changeMode(final boolean sneaking)
    {
        if (sneaking)
        {
            if (this.speed < TileTorcherino.SPEEDS)
                this.speed++;
            else
                this.speed = 0;
        }
        else
        {
            if (this.mode < MODES.length - 1)
                this.mode++;
            else
                this.mode = 0;
        }
    }

    public String getSpeedDescription()
    {
        return this.speed(this.speed) * 100 + "% increase";
    }

    public String getModeDescription()
    {
        return TileTorcherino.MODES[this.mode];
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setBoolean("RequiredRedstoneState", this.requiredRedstoneState);
        tag.setByte("Speed", this.speed);
        tag.setByte("Mode", this.mode);
        tag.setBoolean("PoweredByRedstone", this.poweredByRedstone);
        tag.setInteger("EnergyStored", this.redstoneFlux);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.requiredRedstoneState = tag.getBoolean("RequiredRedstoneState");
        this.speed = tag.getByte("Speed");
        this.mode = tag.getByte("Mode");
        this.poweredByRedstone = tag.getBoolean("PoweredByRedstone");
        this.redstoneFlux = tag.getInteger("EnergyStored");
    }

    @Override
    @Optional.Method(modid = Props.COFH_CORE)
    public final int receiveEnergy(final ForgeDirection from, final int maxReceive, final boolean simulate)
    {
        if (!Torcherino.useRF)
            return 0;

        final int energyReceived = Math.min(TileTorcherino.MAX_ENERGY_STORED - this.redstoneFlux, maxReceive);

        if (!simulate)
        {
            this.redstoneFlux += energyReceived;
        }

        return energyReceived;
    }

    @Override
    @Optional.Method(modid = Props.COFH_CORE)
    public final int extractEnergy(final ForgeDirection from, final int maxExtract, final boolean simulate)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Props.COFH_CORE)
    public final int getEnergyStored(final ForgeDirection from)
    {
        if (!Torcherino.useRF)
            return 0;

        return this.redstoneFlux;
    }

    @Override
    @Optional.Method(modid = Props.COFH_CORE)
    public final int getMaxEnergyStored(final ForgeDirection from)
    {
        if (!Torcherino.useRF)
            return 0;

        return TileTorcherino.MAX_ENERGY_STORED;
    }

    @Override
    @Optional.Method(modid = Props.COFH_CORE)
    public final boolean canConnectEnergy(final ForgeDirection from)
    {
        if (!Torcherino.useRF)
            return false;

        return true;
    }
}
