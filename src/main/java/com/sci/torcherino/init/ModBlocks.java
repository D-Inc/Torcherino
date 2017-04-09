package com.sci.torcherino.init;

import com.sci.torcherino.block.BlockCompressedTorcherino;
import com.sci.torcherino.block.BlockDoubleCompresedTorcherino;
import com.sci.torcherino.block.BlockSupremeTorcherino;
import com.sci.torcherino.block.BlockTorcherino;
import com.sci.torcherino.tile.TileCompressedTorcherino;
import com.sci.torcherino.tile.TileDoubleCompressedTorcherino;
import com.sci.torcherino.tile.TileSupremeTorcherino;
import com.sci.torcherino.tile.TileTorcherino;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;

@GameRegistry.ObjectHolder("Torcherino")
public final class ModBlocks {
    public static BlockTorcherino torcherino;
    public static BlockTorcherino compressedTorcherino;
    public static BlockTorcherino doubleCompressedTorcherino;
    public static BlockTorcherino supremeTorcherino;

    private static Map<String, ItemBlock> remap = new HashMap<String, ItemBlock>();

    public static void init() {
        ModBlocks.torcherino = new BlockTorcherino();
        ModBlocks.compressedTorcherino = new BlockCompressedTorcherino();
        ModBlocks.doubleCompressedTorcherino = new BlockDoubleCompresedTorcherino();
        ModBlocks.supremeTorcherino = new BlockSupremeTorcherino();

        GameRegistry.register(ModBlocks.torcherino.setRegistryName("BlockTorcherino"));
        GameRegistry.register(ModBlocks.compressedTorcherino.setRegistryName("BlockCompressedTorcherino"));
        GameRegistry.register(ModBlocks.doubleCompressedTorcherino.setRegistryName("BlockDoubleCompressedTorcherino"));
        GameRegistry.register(ModBlocks.supremeTorcherino.setRegistryName("BlockSupremeTorcherino"));

        GameRegistry.register(new ItemBlock(ModBlocks.torcherino).setRegistryName(ModBlocks.torcherino.getRegistryName()));
        GameRegistry.register(new ItemBlock(ModBlocks.compressedTorcherino).setRegistryName(ModBlocks.compressedTorcherino.getRegistryName()));
        GameRegistry.register(new ItemBlock(ModBlocks.doubleCompressedTorcherino).setRegistryName(ModBlocks.doubleCompressedTorcherino.getRegistryName()));
        GameRegistry.register(new ItemBlock(ModBlocks.supremeTorcherino).setRegistryName(ModBlocks.supremeTorcherino.getRegistryName()));

        GameRegistry.registerTileEntity(TileTorcherino.class, "torcherino_tile");
        GameRegistry.registerTileEntity(TileCompressedTorcherino.class, "compressed_torcherino_tile");
        GameRegistry.registerTileEntity(TileDoubleCompressedTorcherino.class, "double_compressed_torcherino_tile");
        GameRegistry.registerTileEntity(TileSupremeTorcherino.class, "supreme_torcherino_tile");

        ModBlocks.remap.put("torcherino:tile.torcherino", (ItemBlock) Item.getItemFromBlock(ModBlocks.torcherino));
        ModBlocks.remap.put("torcherino:tile.compressed_torcherino", (ItemBlock) Item.getItemFromBlock(ModBlocks.compressedTorcherino));
        ModBlocks.remap.put("torcherino:tile.double_compressed_torcherino", (ItemBlock) Item.getItemFromBlock(ModBlocks.doubleCompressedTorcherino));
        ModBlocks.remap.put("torcherino:tile.supreme_torcherino", (ItemBlock) Item.getItemFromBlock(ModBlocks.supremeTorcherino));
    }

    public static void initRenders() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.torcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "BlockTorcherino"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.compressedTorcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "BlockCompressedTorcherino"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.doubleCompressedTorcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "BlockDoubleCompressedTorcherino"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.supremeTorcherino), 0, new ModelResourceLocation(new ResourceLocation("torcherino", "BlockSupremeTorcherino"), "inventory"));
    }

    public static void handleMissingMappings(final FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (ModBlocks.remap.containsKey(mapping.name)) {
                if (mapping.type == GameRegistry.Type.ITEM)
                    mapping.remap(ModBlocks.remap.get(mapping.name));
                else
                    mapping.remap(ModBlocks.remap.get(mapping.name).getBlock());
            }
        }
    }

    private ModBlocks() {
    }
}