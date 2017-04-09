package com.sci.torcherino;

import com.sci.torcherino.init.ModBlocks;
import com.sci.torcherino.init.ModRecipes;
import com.sci.torcherino.proxy.CommonProxy;
import com.sci.torcherino.tile.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = "Torcherino", name = "Torcherino", version = "@VERSION@")
public final class Torcherino {
    private static Torcherino instance;

    @Mod.InstanceFactory
    public static Torcherino instance() {
        if (Torcherino.instance == null) {
            Torcherino.instance = new Torcherino();
        }
        return Torcherino.instance;
    }

    @SidedProxy(clientSide = "com.sci.torcherino.proxy.ClientProxy", serverSide = "com.sci.torcherino.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static boolean logPlacement;
    public static boolean overPoweredRecipe;
    public static boolean compressedTorcherino;
    public static boolean doubleCompressedTorcherino;
    public static boolean tripleCompressedTorcherino;
    public static boolean supremeTorcherino;

    private String[] blacklistedBlocks;
    private String[] blacklistedTiles;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent evt) {
        Torcherino.logger = evt.getModLog();

        final File folder = new File(evt.getModConfigurationDirectory(), "sci4me");

        if (!folder.exists())
            folder.mkdir();

        final Configuration cfg = new Configuration(new File(folder, "Torcherino.cfg"));
        try {
            cfg.load();

            Torcherino.logPlacement = cfg.getBoolean("logPlacement", "general", false, "(For Server Owners) Is it logged when someone places a Torcherino?");
            Torcherino.overPoweredRecipe = cfg.getBoolean("overPoweredRecipe", "general", true, "Is the recipe for Torcherino extremely OP?");
            Torcherino.compressedTorcherino = cfg.getBoolean("compressedTorcherino", "general", false, "Is the recipe for the Compressed Torcherino enabled?");
            Torcherino.doubleCompressedTorcherino = cfg.getBoolean("doubleCompressedTorcherino", "general", false, "Is the recipe for the Double Compressed Torcherino enabled? Only takes effect if Compressed Torcherinos are enabled.");
            Torcherino.tripleCompressedTorcherino = cfg.getBoolean("tripleCompressedTorcherino", "general", false, "Is the recipe for the Triple Compressed Torcherino enabled? Only takes effect if Double Compressed Torcherinos are enabled.");
            Torcherino.supremeTorcherino = cfg.getBoolean("supremeTorcherino", "general", false, "Is the recipe for the Supreme Torcherino enabled? Only takes effect if Triple Compressed Torcherinos are enabled.");

            this.blacklistedBlocks = cfg.getStringList("blacklistedBlocks", "blacklist", new String[]{}, "modid:unlocalized");
            this.blacklistedTiles = cfg.getStringList("blacklistedTiles", "blacklist", new String[]{}, "Fully qualified class name");
        } finally {
            if (cfg.hasChanged())
                cfg.save();
        }

        ModBlocks.init();
        ModRecipes.init();

        Torcherino.proxy.preInit();
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent evt) {
        TorcherinoRegistry.blacklistBlock(Blocks.AIR);

        TorcherinoRegistry.blacklistBlock(ModBlocks.torcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.compressedTorcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.doubleCompressedTorcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.tripleCompressedTorcherino);
        TorcherinoRegistry.blacklistBlock(ModBlocks.supremeTorcherino);

        TorcherinoRegistry.blacklistTile(TileTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileCompressedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileDoubleCompressedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileTripleCompressedTorcherino.class);
        TorcherinoRegistry.blacklistTile(TileSupremeTorcherino.class);

        TorcherinoRegistry.blacklistBlock(Blocks.WATER);
        TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_WATER);

        TorcherinoRegistry.blacklistBlock(Blocks.LAVA);
        TorcherinoRegistry.blacklistBlock(Blocks.FLOWING_LAVA);

        Torcherino.proxy.init();
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent evt) {
        for (final String block : this.blacklistedBlocks)
            this.blacklistBlock(block);

        for (final String tile : this.blacklistedTiles)
            this.blacklistTile(tile);

        Torcherino.proxy.postInit();
    }

    @Mod.EventHandler
    public void missingMapping(final FMLMissingMappingsEvent event) {
        ModBlocks.handleMissingMappings(event);
    }

    @Mod.EventHandler
    public void imcMessage(final FMLInterModComms.IMCEvent evt) {
        for (final FMLInterModComms.IMCMessage message : evt.getMessages()) {
            if (!message.isStringMessage()) {
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

    private void blacklistBlock(final String s) {
        final String[] parts = s.split(":");

        if (parts.length != 2) {
            System.out.println("Received malformed message: " + s);
            return;
        }

        final Block block = Block.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));

        if (block == null) {
            System.out.println("Could not find block: " + s + ", ignoring");
            return;
        }

        System.out.println("Blacklisting block: " + block.getUnlocalizedName());

        TorcherinoRegistry.blacklistBlock(block);
    }

    @SuppressWarnings("unchecked")
    private void blacklistTile(final String s) {
        try {
            final Class<?> clazz = this.getClass().getClassLoader().loadClass(s);

            if (clazz == null) {
                System.out.println("Class null: " + s);
                return;
            }

            if (!TileEntity.class.isAssignableFrom(clazz)) {
                System.out.println("Class not a TileEntity: " + s);
                return;
            }

            TorcherinoRegistry.blacklistTile((Class<? extends TileEntity>) clazz);
        } catch (final ClassNotFoundException e) {
            System.out.println("Class not found: " + s + ", ignoring");
        }
    }

    public static final CreativeTabs taberino = new CreativeTabs("Torcherino")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(ModBlocks.doubleCompressedTorcherino);
        }
    };
}