package com.cak.deepslateprocessing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class DPRegistry {
    
    public static BlockEntry<GravelBlock> DEEP_GRAVEL = DeepslateProcessing.REGISTRATE.block("deep_gravel", GravelBlock::new)
        .defaultBlockstate()
        .properties(p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.SNARE).strength(0.6f).sound(SoundType.GRAVEL))
        .simpleItem()
        .register();
    
    public static ItemEntry<Item> DEEPSLATE_SHARD = DeepslateProcessing.REGISTRATE.item("deepslate_shard", Item::new)
        .register();
    
    public static void register() {
    
    }
    
}
