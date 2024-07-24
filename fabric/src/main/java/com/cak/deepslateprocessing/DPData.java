package com.cak.deepslateprocessing;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class DPData implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        Constants.LOG.info("Hello Fabric world!");
        Path resources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
        // fixme re-enable the existing file helper when porting lib's ResourcePackLoader.createPackForMod is fixed
        ExistingFileHelper helper = new ExistingFileHelper(
            Set.of(resources), Set.of("create"), false, null, null
        );
        DeepslateProcessing.REGISTRATE.setupDatagen(gen.createPack(), helper);
    }
}
