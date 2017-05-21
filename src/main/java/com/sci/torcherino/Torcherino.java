package com.sci.torcherino;

import com.sci.torcherino.init.ModBlocks;
import com.sci.torcherino.init.Recipes;
import com.sci.torcherino.lib.Props;
import com.sci.torcherino.tile.*;
import com.sci.torcherino.update.IUpdatableMod;
import com.sci.torcherino.update.ModVersion;
import com.sci.torcherino.update.UpdateChecker;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@Mod(modid = Props.ID, name = Props.NAME, version = Props.VERSION)
public class Torcherino implements IUpdatableMod
{
    private static Torcherino instance;

    public static boolean animatedTextures;
    public static boolean compressedTorcherino;
    public static boolean doubleCompressedTorcherino;
    public static boolean overPoweredRecipe;
    public static boolean logPlacement;
    public static boolean useRF;

    @Mod.InstanceFactory
    public static Torcherino instance()
    {
        if (Torcherino.instance == null)
            Torcherino.instance = new Torcherino();
        return Torcherino.instance;
    }

    public static Logger logger;

    private String[] blacklistedBlocks;
    private String[] blacklistedTiles;

    private Torcherino()
    {
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent evt)
    {

        logger = evt.getModLog();

        final File folder = new File(evt.getModConfigurationDirectory(), "sci4me");

        if (!folder.exists())
            folder.mkdir();

        UpdateChecker.register(this);

        final Configuration cfg = new Configuration(new File(folder, "Torcherino.cfg"));
        try
        {
            cfg.load();

            Torcherino.animatedTextures = cfg.getBoolean("animatedTextures", "visual", true, "Should Torcherino use animated textures?");
            Torcherino.compressedTorcherino = cfg.getBoolean("compressedTorcherino", "general", true, "Are compressed Torcherinos enabled?");
            Torcherino.doubleCompressedTorcherino = cfg.getBoolean("doubleCompressedTorcherin", "general", true, "Are double compressed Torcherinos enabled? Automatically enables compressed Torcherinos.");
            Torcherino.overPoweredRecipe = cfg.getBoolean("overPoweredRecipe", "general", false, "Is the recipe for Torcherino extremely OP?");
            Torcherino.logPlacement = cfg.getBoolean("logPlacement", "general", false, "(For Server Owners) Is it logged when someone places a Torcherino?");
            Torcherino.useRF = cfg.getBoolean("useRF", "general", false, "Do Torcherinos require Redstone Flux to run?");

            if(Torcherino.doubleCompressedTorcherino)
                Torcherino.compressedTorcherino = true;

            this.blacklistedBlocks = cfg.getStringList("blacklistedBlocks", "blacklist", new String[]{}, "modid:unlocalized");
            this.blacklistedTiles = cfg.getStringList("blacklistedTiles", "blacklist", new String[]{}, "Fully qualified class name");
        }
        finally
        {
            if (cfg.hasChanged())
                cfg.save();
        }

        ModBlocks.init();
        Recipes.init();
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent evt)
    {
        TorcherinoRegistry.blacklistBlock(Blocks.air);

        TorcherinoRegistry.blacklistBlock(ModBlocks.torcherino);

        if (ModBlocks.compressedTorcherino != null)
        {
            TorcherinoRegistry.blacklistBlock(ModBlocks.compressedTorcherino);
            TorcherinoRegistry.blacklistBlock(ModBlocks.compressedInverseTorcherino);
        }

        if (ModBlocks.doubleCompressedTorcherino != null)
        {
            TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedTorcherino);
            TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedInverseTorcherino);
        }

        TorcherinoRegistry.blacklistTile(TileTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileCompressedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileDoubleCompressedTorcherino.class);

        TorcherinoRegistry.blacklistTile(TileInvertedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileCompressedInvertedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileDoubleCompressedInvertedTorcherino.class);

        TorcherinoRegistry.blacklistBlock(Blocks.water);
        TorcherinoRegistry.blacklistBlock(Blocks.flowing_water);

        TorcherinoRegistry.blacklistBlock(Blocks.lava);
        TorcherinoRegistry.blacklistBlock(Blocks.flowing_lava);
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent evt)
    {
        for (final String block : this.blacklistedBlocks)
            this.blacklistBlock(block);

        for (final String tile : this.blacklistedTiles)
            this.blacklistTile(tile);
    }

    private void blacklistBlock(final String s)
    {
        final String[] parts = s.split(":");

        if (parts.length != 2)
        {
            System.out.println("Received malformed message: " + s);
            return;
        }

        final Block block = GameRegistry.findBlock(parts[0], parts[1]);

        if (block == null)
        {
            System.out.println("Could not find block: " + s + ", ignoring");
            return;
        }

        System.out.println("Blacklisting block: " + block.getUnlocalizedName());

        TorcherinoRegistry.blacklistBlock(block);
    }

    @SuppressWarnings("unchecked")
    private void blacklistTile(final String s)
    {
        try
        {
            final Class<?> clazz = this.getClass().getClassLoader().loadClass(s);

            if (clazz == null)
            {
                System.out.println("Class null: " + s);
                return;
            }

            if (!TileEntity.class.isAssignableFrom(clazz))
            {
                System.out.println("Class not a TileEntity: " + s);
                return;
            }

            TorcherinoRegistry.blacklistTile((Class<? extends TileEntity>) clazz);
        }
        catch (final ClassNotFoundException e)
        {
            System.out.println("Class not found: " + s + ", ignoring");
        }
    }

    @Mod.EventHandler
    public void imcMessage(final FMLInterModComms.IMCEvent evt)
    {
        for (final FMLInterModComms.IMCMessage message : evt.getMessages())
        {
            if (!message.isStringMessage())
            {
                System.out.println("Received non-string message! Ignoring");
                continue;
            }

            final String s = message.getStringValue();

            if (message.key.equals("blacklist-block"))
                this.blacklistBlock(s);
            else if (message.key.equals("blacklist-tile"))
                this.blacklistTile(s);
        }
    }

    @Override
    public String name()
    {
        return Props.NAME;
    }

    @Override
    public String updateURL()
    {
        return Props.UPDATE_URL;
    }

    @Override
    public ModVersion version()
    {
        return ModVersion.parse(Props.VERSION);
    }
}