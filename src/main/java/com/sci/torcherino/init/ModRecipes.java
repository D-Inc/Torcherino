package com.sci.torcherino.init;

import com.sci.torcherino.Torcherino;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModRecipes {
    public static void init() {
        if (Torcherino.overPoweredRecipe) {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.torcherino), "XCX", "CTC", "XCX", 'C', Items.CLOCK, 'T', Blocks.TORCH);
        } else {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.torcherino), "SCS", "CTC", "SCS", 'C', Items.CLOCK, 'S', Items.NETHER_STAR, 'T', Blocks.TORCH);
        }

        if (Torcherino.compressedTorcherino)
        {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.compressedTorcherino), new Object[]{"ttt", "ttt", "ttt", 't', ModBlocks.torcherino});

            if (Torcherino.doubleCompressedTorcherino)
            {
                GameRegistry.addRecipe(new ItemStack(ModBlocks.doubleCompressedTorcherino), new Object[]{"ttt", "ttt", "ttt", 't', ModBlocks.compressedTorcherino});

                if (Torcherino.tripleCompressedTorcherino)
                {
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.tripleCompressedTorcherino), new Object[]{"ttt", "ttt", "ttt", 't', ModBlocks.doubleCompressedTorcherino});

                    if (Torcherino.supremeTorcherino)
                    {
                        GameRegistry.addRecipe(new ItemStack(ModBlocks.supremeTorcherino), new Object[]{"ttt", "ttt", "ttt", 't', ModBlocks.tripleCompressedTorcherino});
                    }
                }
            }
        }
    }

    private ModRecipes() {
    }
}