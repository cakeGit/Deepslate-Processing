package com.cak.deepslateprocessing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DPTabs {
    
    public static final TabInfo DEEPSLATE_PROCESSING = register("deepslate_processing",
        () -> FabricItemGroup.builder()
            .title(Components.translatable("itemGroup.deepslate_processing"))
            .icon(() -> DPRegistry.DEEP_GRAVEL.asStack())
            .displayItems(new RegistrateDisplayItemsGenerator(true, () -> DPTabs.DEEPSLATE_PROCESSING))
            .build());
    
    private static TabInfo register(String name, Supplier<CreativeModeTab> supplier) {
        ResourceLocation id = Create.asResource(name);
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
        CreativeModeTab tab = supplier.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        return new TabInfo(key, tab);
    }
    
    public static void register() {
        // fabric: just load the class
    }
    
    private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        private static final Predicate<Item> IS_ITEM_3D_PREDICATE;
        
        static {
            MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
            EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
                isItem3d.setValue(item -> {
                    ItemRenderer itemRenderer = Minecraft.getInstance()
                        .getItemRenderer();
                    BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                    return model.isGui3d();
                });
            });
            IS_ITEM_3D_PREDICATE = isItem3d.getValue();
        }
        
        @Environment(EnvType.CLIENT)
        private static Predicate<Item> makeClient3dItemPredicate() {
            return item -> {
                ItemRenderer itemRenderer = Minecraft.getInstance()
                    .getItemRenderer();
                BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                return model.isGui3d();
            };
        }
        
        private final boolean addItems;
        private final Supplier<TabInfo> tabFilter;
        
        public RegistrateDisplayItemsGenerator(boolean addItems, Supplier<TabInfo> tabFilter) {
            this.addItems = addItems;
            this.tabFilter = tabFilter;
        }
        
        private static Function<Item, ItemStack> makeStackFunc() {
            return ItemStack::new;
        }
        
        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
            Function<Item, ItemStack> stackFunc = makeStackFunc();
            
            List<Item> items = new LinkedList<>();
            if (addItems) {
                items.addAll(collectItems(IS_ITEM_3D_PREDICATE.negate()));
            }
            items.addAll(collectBlocks(item -> false));
            if (addItems) {
                items.addAll(collectItems(IS_ITEM_3D_PREDICATE));
            }
            
            outputAll(output, items, stackFunc);
        }
        
        private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Block> entry : DeepslateProcessing.REGISTRATE.getAll(Registries.BLOCK)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter.get().key()))
                    continue;
                Item item = entry.get()
                    .asItem();
                if (item == Items.AIR)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
            return items;
        }
        
        private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Item> entry : DeepslateProcessing.REGISTRATE.getAll(Registries.ITEM)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter.get().key()))
                    continue;
                Item item = entry.get();
                if (item instanceof BlockItem)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            return items;
        }
        
        private static void applyOrderings(List<Item> items, List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings) {
            for (RegistrateDisplayItemsGenerator.ItemOrdering ordering : orderings) {
                int anchorIndex = items.indexOf(ordering.anchor());
                if (anchorIndex != -1) {
                    Item item = ordering.item();
                    int itemIndex = items.indexOf(item);
                    if (itemIndex != -1) {
                        items.remove(itemIndex);
                        if (itemIndex < anchorIndex) {
                            anchorIndex--;
                        }
                    }
                    if (ordering.type() == RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER) {
                        items.add(anchorIndex + 1, item);
                    } else {
                        items.add(anchorIndex, item);
                    }
                }
            }
        }
        
        private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc) {
            for (Item item : items) {
                output.accept(stackFunc.apply(item));
            }
        }
        
        private record ItemOrdering(Item item, Item anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type type) {
            public static RegistrateDisplayItemsGenerator.ItemOrdering before(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.BEFORE);
            }
            
            public static RegistrateDisplayItemsGenerator.ItemOrdering after(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER);
            }
            
            public enum Type {
                BEFORE,
                AFTER;
            }
        }
    }
    
    public record TabInfo(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
    }
}
