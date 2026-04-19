package zettasword.zetta_spells.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.zetta_spells.ZettaSpellsMod;

public class ZSBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ZettaSpellsMod.MODID);

    public static final RegistryObject<Block> PASSABLE_BLOCK = BLOCKS.register("passable_block",
            () -> new PassableBlock(Block.Properties.copy(Blocks.DARK_OAK_FENCE)
                    .lightLevel((state) -> 15)
                    .noOcclusion()
                    .strength(0.1f)
                    .sound(SoundType.WOOL)));

    public static final RegistryObject<Block> ORE_CONVERSION_BLOCK = BLOCKS.register("ore_conversion_block",
            () -> new OreConversionBlock(Block.Properties.copy(Blocks.WHITE_CARPET)
                    .lightLevel((state) -> 15)
                    .noOcclusion()
                    .strength(0.1f)
                    .sound(SoundType.WOOL)));

    public static final RegistryObject<Block> LEVITATION_SIGIL_BLOCK = BLOCKS.register("levitation_sigil_block",
            () -> new LevitationSigilBlock(Block.Properties.copy(Blocks.WHITE_CARPET)
                    .lightLevel((state) -> 15)
                    .noOcclusion()
                    .strength(0.1f)
                    .sound(SoundType.WOOL), 0));

    public static final RegistryObject<Block> ADVANCED_LEVITATION_SIGIL_BLOCK = BLOCKS.register("advanced_levitation_sigil_block",
            () -> new LevitationSigilBlock(Block.Properties.copy(Blocks.WHITE_CARPET)
                    .lightLevel((state) -> 15)
                    .noOcclusion()
                    .strength(0.1f)
                    .sound(SoundType.WOOL), 9));

}