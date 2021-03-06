package minechem.init;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModConfig {
	// Config file
	public static Configuration config;

	// --------
	// FEATURES
	// --------
	// Determines if the mod will generate ore at all.
	public static boolean generateOre = true;

	// Size of Uranium ore clusters
	public static int UraniumOreClusterSize = 3;

	// How many times per chunk will uranium attempt to generate?
	public static int UraniumOreDensity = 5;

	// Determines if the mod will print out tons of extra information while running.
	public static boolean DebugMode = false;

	// Determines how far away in blocks a packet will be sent to players in a given dimension to reduce packet spam.
	public static int UpdateRadius = 20;

	// Enabling automation can allow duping. Enabled by default.
	public static boolean AllowAutomation = true;

	// Multiplier for half life of elements and molecules
	public static int halfLifeMultiplier = 100;

	// Depth of recursive recipe gen
	public static int recursiveDepth = 10;

	//Renames ChemicalTurtles to "Jenkins"
	public static boolean advancedTurtleAI = false;

	// Disabling of enchants, food spiking, etc
	public static boolean vialPlacing = true;
	public static boolean FoodSpiking = true;
	public static boolean SwordEffects = true;
	public static boolean fluidEffects = true;
	public static boolean decaySafeMachines = false;
	public static boolean recreationalChemicalEffects = true;

	// Power usage
	public static boolean powerUseEnabled = true;
	public static int costDecomposition = 1000;
	public static int synthesisMultiplier = 10;
	public static int fusionMultiplier = 100;
	public static int fissionMultiplier = 100;
	public static int energyPacketSize = 100;

	// Power base storage values
	public static int maxSynthesizerStorage = 100000;
	public static int maxDecomposerStorage = 100000;
	public static int maxFissionStorage = 100000;
	public static int maxFusionStorage = 100000;

	// Chemical Explosion
	public static boolean reactionItemMeetFluid = true;
	public static boolean reactionFluidMeetFluid = true;

	// Enable Water Bucket --> 8 H2O recipe in crafting grid
	public static boolean enableWaterBucketIntoH2ORecipe = true;

	// NEI support
	public static boolean supportNEI = true;

	//Blacklisting
	public static String[] DecomposerBlacklist = {};
	public static String[] SynthesisMachineBlacklist = {};
	public static NonNullList<ItemStack> decomposerBlacklist = NonNullList.create();
	public static NonNullList<ItemStack> synthesisBlacklist = NonNullList.create();
	public static int[] worldGenDimWhitelist = new int[] {
			0
	};

	public static boolean displayMoleculeEffects = true;

	public static boolean decomposeChemicalFluids = true;

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			loadConfig();
		}
	}

	public static void loadConfig() {
		Property prop;
		List<String> configList = new ArrayList<String>();

		LanguageMap languageMap = new LanguageMap();

		config.addCustomCategoryComment("worldgen", languageMap.translateKey("config.worldgen.description"));
		config.addCustomCategoryComment("blacklist", languageMap.translateKey("config.blacklist.description"));
		config.addCustomCategoryComment("power", languageMap.translateKey("config.power.description"));
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, languageMap.translateKey("config.general.description"));

		prop = config.get(Configuration.CATEGORY_GENERAL, "displayMoleculeEffects", ModConfig.displayMoleculeEffects);
		prop.setComment(languageMap.translateKey("config.moleculeEffects.description"));
		prop.setLanguageKey("config.moleculeEffects.name");
		displayMoleculeEffects = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get("worldgen", "generateOre", ModConfig.generateOre);
		prop.setComment(languageMap.translateKey("config.worldgen.ore.description"));
		prop.setLanguageKey("config.worldgen.ore.tooltip");
		generateOre = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get("worldgen", "uraniumOreClusterSize", ModConfig.UraniumOreClusterSize);
		prop.setMinValue(1).setMaxValue(10);
		prop.setComment(languageMap.translateKey("config.uraniumoreclustersize.description"));
		prop.setLanguageKey("config.uraniumoreclustersize");
		UraniumOreClusterSize = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("worldgen", "DimensionWHitelistForWorldGen", ModConfig.worldGenDimWhitelist);
		prop.setComment(languageMap.translateKey("config.worldgen.dimwhitelist.description"));
		prop.setLanguageKey("config.worldgen.dimwhitelist.tooltip");
		worldGenDimWhitelist = prop.getIntList();
		configList.add(prop.getName());

		prop = config.get("worldgen", "uraniumoredensity", ModConfig.UraniumOreDensity);
		prop.setMinValue(1).setMaxValue(64);
		prop.setComment(languageMap.translateKey("config.uraniumoredensity.description"));
		prop.setLanguageKey("config.uraniumoredensity");
		UraniumOreDensity = prop.getInt();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "debugMode", ModConfig.DebugMode);
		prop.setComment(languageMap.translateKey("config.debugmode.description"));
		prop.setLanguageKey("config.debugmode");
		DebugMode = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "advancedTurtleAI", ModConfig.advancedTurtleAI);
		prop.setComment(languageMap.translateKey("config.advancedTurtleAI.description"));
		prop.setLanguageKey("config.advancedTurtleAI.name");
		advancedTurtleAI = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "updateRadius", ModConfig.UpdateRadius);
		prop.setMinValue(1).setMaxValue(50);
		prop.setComment(languageMap.translateKey("config.updateradius.description"));
		prop.setLanguageKey("config.updateradius");
		UpdateRadius = prop.getInt();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "allowAutomation", ModConfig.AllowAutomation);
		prop.setComment(languageMap.translateKey("config.allowautomation.description"));
		prop.setLanguageKey("config.allowautomation");
		AllowAutomation = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "vialPlacing", ModConfig.vialPlacing);
		prop.setComment(languageMap.translateKey("config.vialplacing.description"));
		prop.setLanguageKey("config.vialplacing");
		vialPlacing = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "foodSpiking", ModConfig.FoodSpiking);
		prop.setComment(languageMap.translateKey("config.foodspiking.description"));
		prop.setLanguageKey("config.foodspiking");
		FoodSpiking = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "swordEffects", ModConfig.SwordEffects);
		prop.setComment(languageMap.translateKey("config.swordeffects.description"));
		prop.setLanguageKey("config.swordeffects");
		SwordEffects = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "halfLifeMultiplier", ModConfig.UpdateRadius);
		prop.setMinValue(1).setMaxValue(200);
		prop.setComment(languageMap.translateKey("config.halfLifeMultiplier.description"));
		prop.setLanguageKey("config.halfLifeMultiplier.name");
		halfLifeMultiplier = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "recursiveDepth", ModConfig.recursiveDepth);
		prop.setMinValue(3).setMaxValue(20).requiresMcRestart();
		prop.setComment(languageMap.translateKey("config.recursiveDepth.description"));
		prop.setLanguageKey("config.recursiveDepth.name");
		recursiveDepth = prop.getInt();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "decaySafeMachines", ModConfig.SwordEffects);
		prop.setComment(languageMap.translateKey("config.decaySafeMachines.description"));
		prop.setLanguageKey("config.decaySafeMachines.name");
		decaySafeMachines = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "recreationalChemicalEffects", ModConfig.recreationalChemicalEffects);
		prop.setComment(languageMap.translateKey("config.recreationalChemicalEffects.description"));
		prop.setLanguageKey("config.recreationalChemicalEffects");
		recreationalChemicalEffects = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "fluidEffects", ModConfig.SwordEffects);
		prop.setComment(languageMap.translateKey("config.fluideffects.description"));
		prop.setLanguageKey("config.fluideffects.name");
		fluidEffects = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "reactionItemMeetFluid", ModConfig.reactionItemMeetFluid);
		prop.setComment(languageMap.translateKey("config.reactionItemMeetFluid.description"));
		prop.setLanguageKey("config.reactionItemMeetFluid");
		reactionItemMeetFluid = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "reactionFluidMeetFluid", ModConfig.reactionFluidMeetFluid);
		prop.setComment(languageMap.translateKey("config.reactionFluidMeetFluid.description"));
		prop.setLanguageKey("config.reactionFluidMeetFluid");
		reactionFluidMeetFluid = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "supportNEI", ModConfig.reactionFluidMeetFluid);
		prop.setComment(languageMap.translateKey("config.supportNEI.description"));
		prop.setLanguageKey("config.supportNEI.name");
		supportNEI = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get(Configuration.CATEGORY_GENERAL, "enableWaterBucketIntoH2ORecipe", ModConfig.enableWaterBucketIntoH2ORecipe);
		prop.setComment(languageMap.translateKey("config.enableWaterBucketIntoH2ORecipe.description"));
		prop.setLanguageKey("config.enableWaterBucketIntoH2ORecipe.name");
		enableWaterBucketIntoH2ORecipe = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get("blacklist", "decomposition", new String[] {
				"minecraft:dirt"
		});
		prop.setLanguageKey("config.blacklist.decomposition.tooltip").setRequiresMcRestart(true);
		prop.setComment(languageMap.translateKey("config.blacklist.decomposition.example"));
		DecomposerBlacklist = prop.getStringList();

		configList.add(prop.getName());

		prop = config.get("blacklist", "synthesis", new String[] {
				"minecraft:diamond", "ore:ore*", "*:dragon_egg"
		});
		prop.setLanguageKey("config.blacklist.synthesis.tooltip").setRequiresMcRestart(true);
		prop.setComment(languageMap.translateKey("config.blacklist.synthesis.example"));
		SynthesisMachineBlacklist = prop.getStringList();

		configList.add(prop.getName());

		prop = config.get("power", "enable", ModConfig.powerUseEnabled);
		prop.setComment(languageMap.translateKey("config.power.enable.description"));
		prop.setLanguageKey("config.power.enable.name").setRequiresMcRestart(true);
		powerUseEnabled = prop.getBoolean();
		configList.add(prop.getName());

		prop = config.get("power", "maxDecomposerStorage", ModConfig.maxDecomposerStorage);
		prop.setComment(languageMap.translateKey("config.power.decomposer.max.description"));
		prop.setLanguageKey("config.power.decomposer.max.name");
		maxDecomposerStorage = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "costDecomposition", ModConfig.costDecomposition);
		prop.setMinValue(1).setMaxValue(ModConfig.maxDecomposerStorage);
		prop.setComment(languageMap.translateKey("config.power.decomposer.cost.description"));
		prop.setLanguageKey("config.power.decomposer.cost.name");
		costDecomposition = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "maxSynthesizerStorage", ModConfig.maxSynthesizerStorage);
		prop.setComment(languageMap.translateKey("config.power.synthesizer.max.description"));
		prop.setLanguageKey("config.power.synthesizer.max.name");
		maxSynthesizerStorage = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "energyPacketSize", ModConfig.energyPacketSize);
		prop.setComment(languageMap.translateKey("config.power.energyPacketSize.description"));
		prop.setLanguageKey("config.power.energyPacketSize.max.name");
		energyPacketSize = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "costSythesisMultiplier", ModConfig.synthesisMultiplier);
		prop.setMinValue(1).setMaxValue(100);
		prop.setComment(languageMap.translateKey("config.power.synthesizer.cost.description"));
		prop.setLanguageKey("config.power.synthesizer.cost.name");
		synthesisMultiplier = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "costFissionMultiplier", ModConfig.fissionMultiplier);
		prop.setMinValue(1).setMaxValue(100);
		prop.setComment(languageMap.translateKey("config.power.fission.cost.description"));
		prop.setLanguageKey("config.power.fusion.cost.name");
		fissionMultiplier = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "maxFissionStorage", ModConfig.maxFissionStorage);
		prop.setComment(languageMap.translateKey("config.power.fission.max.description"));
		prop.setLanguageKey("config.power.fission.max.name");
		maxFissionStorage = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "costFusionMultiplier", ModConfig.fusionMultiplier);
		prop.setMinValue(1).setMaxValue(100);
		prop.setComment(languageMap.translateKey("config.power.fusion.cost.description"));
		prop.setLanguageKey("config.power.fusion.cost.name");
		fusionMultiplier = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("power", "maxFusionStorage", ModConfig.maxFusionStorage);
		prop.setComment(languageMap.translateKey("config.power.fusion.max.description"));
		prop.setLanguageKey("config.power.fusion.max.name");
		maxFusionStorage = prop.getInt();
		configList.add(prop.getName());

		prop = config.get("blacklist", "decomposeFluidChemicals", ModConfig.decomposeChemicalFluids);
		prop.setComment(languageMap.translateKey("config.decomposeFluidChemicals.description"));
		prop.setLanguageKey("config.decomposeFluidChemicals.name");
		ModConfig.decomposeChemicalFluids = prop.getBoolean();
		configList.add(prop.getName());

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(new ConfigElement(config.getCategory("worldgen")).getChildElements());
		list.addAll(new ConfigElement(config.getCategory("blacklist")).getChildElements());
		list.addAll(new ConfigElement(config.getCategory("power")).getChildElements());
		list.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		return list;
	}
}
