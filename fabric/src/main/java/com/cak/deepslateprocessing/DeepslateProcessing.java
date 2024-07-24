package com.cak.deepslateprocessing;

import com.mojang.datafixers.kinds.Const;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;

public class DeepslateProcessing implements ModInitializer {
    
    public static CreateRegistrate REGISTRATE = CreateRegistrate.create(Constants.MOD_ID)
		    .setCreativeTab(DPTabs.DEEPSLATE_PROCESSING.key());
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        DPLang.register();
        CommonClass.init();
        DPTabs.register();
        DPRegistry.register();
        REGISTRATE.register();
    }
    
}
