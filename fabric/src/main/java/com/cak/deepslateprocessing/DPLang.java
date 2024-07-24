package com.cak.deepslateprocessing;

public class DPLang {
    
    public static void register() {
        add("itemGroup.deepslate_processing", "Create: Deepslate Processing");
    }
    
    public static void add(String key, String value) {
        DeepslateProcessing.REGISTRATE.addRawLang(key, value);
    }
    
}
