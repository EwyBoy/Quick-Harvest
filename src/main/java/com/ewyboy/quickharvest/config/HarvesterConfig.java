package com.ewyboy.quickharvest.config;

import com.ewyboy.quickharvest.QuickHarvest;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class HarvesterConfig {
    private final BooleanValue enabled;
    private final BooleanValue requiresTool;
    private final BooleanValue takeReplantItem;
    private final ConfigValue<String> validToolType;
    private final ConfigValue<String> replantItem;
    private final String name;

    public HarvesterConfig(String name, Builder server, boolean enabled, boolean requiresTool, boolean takeReplantItem, ToolType toolType, String replantDefault) {
        this.name = name;
        server.push(name);

        this.enabled = server
                .comment("If true, this harvester will be enabled.")
                .translation(String.format("config.%s.%s.enabled", QuickHarvest.ID, name))
                .worldRestart()
                .define("enabled", enabled);

        this.requiresTool = server
                .comment("If true, this harvester will require the set tool to function.")
                .translation(String.format("config.%s.%s.requires_tool", QuickHarvest.ID, name))
                .worldRestart()
                .define("requires_tool", requiresTool);

        this.takeReplantItem = server
                .comment("If true, this harvester will take a replant item when performing a quick harvest.")
                .translation(String.format("config.%s.%s.take_replant_item", QuickHarvest.ID, name))
                .worldRestart()
                .define("take_replant_item", takeReplantItem);

        this.validToolType = server
                .comment("A string representing the type of tool required to use the harvester.")
                .comment("NOTE: Only works if 'require_tool' is set to true")
                .comment("Recommended values: 'hoe', 'axe', 'shovel', 'pickaxe'")
                .translation(String.format("config.%s.%s.valid_tool_type", QuickHarvest.ID, name))
                .define("valid_tool_type", toolType == null ? "" : toolType.getName());

        this.replantItem = server
                .comment("The registry name of the item that is required to replant after a quick harvest.")
                .comment("NOTE: This only works if 'take_replant_item' is true.")
                .translation(String.format("config.%s.%s.replant_item", QuickHarvest.ID, name))
                .worldRestart()
                .define("replant_item", replantDefault, str -> Objects.nonNull(str) && ResourceLocation.isResouceNameValid((String) str));

        server.pop();
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public boolean requiresTool() {
        return this.requiresTool.get();
    }

    public boolean takesReplantItem() {
        return this.takeReplantItem.get();
    }

    public ToolType getToolType() {
        final String toolType = this.validToolType.get();
        if (toolType.trim().isEmpty()) {
            return null;
        }
        return ToolType.get(toolType);
    }

    public Item getReplantItem() {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(replantItem.get()));
    }

    public String getName() {
        return name;
    }
}
