package com.ewyboy.quickharvest.config;

import com.ewyboy.quickharvest.QuickHarvest;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

public class HarvesterConfig {

  private final BooleanValue enabled;
  private final BooleanValue requiresTool;
  private final BooleanValue takeReplantItem;
  private final ConfigValue<String> validToolType;
  private final ConfigValue<List<? extends String>> harvestBlacklist;
  private final ConfigValue<String> replantItem;
  private final String name;

  public HarvesterConfig(String name, Builder configBuilder, boolean enabled, boolean requiresTool, boolean takeReplantItem, ToolType toolType, String replantDefault, List<Item> harvestBlacklist) {
    this.name = name;
    configBuilder.push(name);

    this.enabled = configBuilder
        .comment("If true, this harvester will be enabled.")
        .translation(String.format("config.%s.%s.enabled", QuickHarvest.ID, name))
        .worldRestart()
        .define("enabled", enabled);

    this.requiresTool = configBuilder
        .comment("If true, this harvester will require the set tool to function.")
        .translation(String.format("config.%s.%s.requires_tool", QuickHarvest.ID, name))
        .worldRestart()
        .define("requires_tool", requiresTool);

    this.takeReplantItem = configBuilder
        .comment("If true, this harvester will take a replant item when performing a quick harvest.")
        .translation(String.format("config.%s.%s.take_replant_item", QuickHarvest.ID, name))
        .worldRestart()
        .define("take_replant_item", takeReplantItem);

    this.validToolType = configBuilder
        .comment("A string representing the type of tool required to use the harvester.")
        .comment("NOTE: Only works if 'require_tool' is set to true")
        .comment("Recommended values: 'hoe', 'axe', 'shovel', 'pickaxe'")
        .translation(String.format("config.%s.%s.valid_tool_type", QuickHarvest.ID, name))
        .define("valid_tool_type", toolType == null ? "" : toolType.getName());

    this.replantItem = configBuilder
        .comment("The registry name of the item that is required to replant after a quick harvest.")
        .comment("NOTE: This only works if 'take_replant_item' is true.")
        .translation(String.format("config.%s.%s.replant_item", QuickHarvest.ID, name))
        .worldRestart()
        .define("replant_item", replantDefault, str -> Objects.nonNull(str) && isValidResourceLocationString((String) str));

    this.harvestBlacklist = configBuilder
        .comment("A List of strings representing the items which when held, this harvester will not work." + (harvestBlacklist.isEmpty() ? "" : String.format("%nRecommended values: %s", harvestBlacklist.stream().map(Item::getRegistryName).filter(Objects::nonNull).map(ResourceLocation::toString).map(s -> '"' + s + '"').collect(Collectors.toList()).toString())))
        .translation(String.format("config.%s.%s.harvest_blacklist", QuickHarvest.ID, name))
        .defineList("harvest_blacklist",
            harvestBlacklist.stream().map(Item::getRegistryName).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.toList()),
            it -> ForgeRegistries.ITEMS.containsKey(new ResourceLocation((String) it)));

    configBuilder.pop();
  }

  public List<Item> getBlacklist() {
    return this.harvestBlacklist.get()
        .stream()
        .map(ResourceLocation::new)
        .map(ForgeRegistries.ITEMS::getValue)
        .collect(Collectors.toList());
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
    return ForgeRegistries.ITEMS.getValue(new ResourceLocation(replantItem.get()));
  }

  public String getName() {
    return name;
  }

  public static boolean isValidResourceLocationString(String resourceName) {
    String[] parts = splitResourceLocationString(resourceName, ':');
    return isValidResourceLocationNamespace(parts[0]) && isValidResourceLocationPath(parts[1]);
  }

  public static boolean isValidResourceLocationNamespace(String namespace) {
    for (char ch : namespace.toCharArray()) {
      if (!isValidResourceLocationChar(ch, false)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isValidResourceLocationPath(String path) {
    for (char ch : path.toCharArray()) {
      if (!isValidResourceLocationChar(ch, true)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isValidResourceLocationChar(char ch, boolean isPath) {
    return ch == '_'
        || ch == '-'
        || ch >= 'a' && ch <= 'z'
        || ch >= '0' && ch <= '9'
        || ch == '.'
        || ch == '/' && isPath;
  }

  public static String[] splitResourceLocationString(String name, char splitter) {
    String[] loc = new String[]{"minecraft", name};

    int splitterIndex = name.indexOf(splitter);
    if (splitterIndex >= 0) {
      loc[1] = name.substring(splitterIndex + 1);
      if (splitterIndex >= 1) {
        loc[0] = name.substring(0, splitterIndex);
      }
    }

    return loc;
  }
}
