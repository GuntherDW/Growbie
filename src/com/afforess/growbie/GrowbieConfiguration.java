package com.afforess.growbie;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class GrowbieConfiguration {
	private static HashMap<Material,Integer> growablePlants;
	private static HashMap<Material,Material> growableBlocks;
	private static HashMap<Material,Material> spreadableBlocks;
    private static HashMap<Material,Material> spreadablePlants;
	private static Boolean betterTrees = false;
    private static YamlConfiguration globalConfig = new YamlConfiguration();
	
	
	public static void checkConfigFile() {
		// create config file if it doesn't exist
		File configFile = new File(Growbie.instance.getDataFolder(), "config.yml");
		if (!configFile.canRead()) {
            try {
                configFile.getParentFile().mkdirs();
                JarFile jar = new JarFile(Growbie.growbie);
                JarEntry entry = jar.getJarEntry("config.yml");
                InputStream is = jar.getInputStream(entry);
                FileOutputStream os = new FileOutputStream(configFile);
                byte[] buf = new byte[(int)entry.getSize()];
                is.read(buf, 0, (int) entry.getSize());
                os.write(buf);
                os.close();
                globalConfig.load(configFile);
            } catch (Exception e) {
                System.out.println("Growbie: could not create configuration file");
            }
        } else {
            try {
                System.out.println("[Growbie] Loading file "+configFile.getAbsolutePath());
                globalConfig.load(configFile);
            } catch(Exception e) {
                System.out.println("Growbie: could not read configuration file");
                e.printStackTrace();
            }
        }

		// load stuff
		growablePlants = new HashMap<Material,Integer>();
		growableBlocks = new HashMap<Material, Material>();
		spreadableBlocks = new HashMap<Material, Material>();
        spreadablePlants = new HashMap<Material, Material>();
		
		try {
			HashMap<?, ?> srcGrowablePlants = (HashMap<?, ?>)globalConfig.getConfigurationSection("growable_plants").getValues(false);
			HashMap<?, ?> srcGrowableBlocks = (HashMap<?, ?>)globalConfig.getConfigurationSection("growable_blocks").getValues(false);
			HashMap<?, ?> srcSpreadableBlocks = (HashMap<?, ?>)globalConfig.getConfigurationSection("spreadable_blocks").getValues(false);
            HashMap<?, ?> srcSpreadablePlants = (HashMap<?, ?>)globalConfig.getConfigurationSection("spreadable_plants").getValues(false);
			Iterator<?> i;
			Entry<?, ?> e;


			// load plants
			i = srcGrowablePlants.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material m = Material.getMaterial(e.getKey().toString());
				if (m == null && e.getKey() instanceof Integer) m = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m != null) growablePlants.put(m, (Integer)e.getValue());
			}

			// load growable blocks
			i = srcGrowableBlocks.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material[] m = {Material.getMaterial(e.getKey().toString()), Material.getMaterial(e.getValue().toString())};
				if (m[0] == null && e.getKey() instanceof Integer) m[0] = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m[1] == null && e.getValue() instanceof Integer) m[1] = Material.getMaterial(((Integer)e.getValue()).intValue());
				if (m.length == 2 && m[0] != null && m[1] != null) growableBlocks.put(m[0], m[1]);
			}
			
			// load spreadable blocks
			i = srcSpreadableBlocks.entrySet().iterator();
			while (i.hasNext()) {
				e = (Entry<?, ?>)i.next();
				Material[] m = {Material.getMaterial(e.getKey().toString()), Material.getMaterial(e.getValue().toString())};
				if (m[0] == null && e.getKey() instanceof Integer) m[0] = Material.getMaterial(((Integer)e.getKey()).intValue());
				if (m[1] == null && e.getValue() instanceof Integer) m[1] = Material.getMaterial(((Integer)e.getValue()).intValue());
				if (m.length == 2 && m[0] != null && m[1] != null) spreadableBlocks.put(m[0], m[1]);
			}

            // load spreadable plants
            i = srcSpreadablePlants.entrySet().iterator();
            while (i.hasNext()) {
                e = (Entry<?, ?>)i.next();
                Material[] m = { Material.getMaterial(e.getKey().toString()), Material.getMaterial(e.getValue().toString()) };
                if (m[0] == null && e.getKey() instanceof Integer) m[0] = Material.getMaterial(((Integer)e.getKey()).intValue());
                if (m[1] == null && e.getValue() instanceof Integer) m[1] = Material.getMaterial(((Integer)e.getValue()).intValue());
                if (m.length == 2 && m[0] != null && m[1] != null) spreadablePlants.put(m[0], m[1]);
            }
			
			// load better trees option
			betterTrees = globalConfig.getBoolean("better_trees", false);
			
		} catch (Exception e) {
			System.out.println("Growbie: error loading configuration");
            e.printStackTrace();
		}
	}
	
	public static boolean isGrowablePlant(Material m) {
		return growablePlants.containsKey(m);
	}

	public static int plantGrowthRate(Material m) {
		return growablePlants.get(m).intValue();
	}

	public static boolean canGrowPlantOnBlock(Block b) {
		return canGrowPlantOnBlock(b, false);
	}

    public static boolean canGrowPlantOnBlock(Block b, boolean sand) {
        Block under = b.getRelative(BlockFace.DOWN);
        return (b.getType() == Material.AIR && (under.getType() == Material.DIRT || under.getType() == Material.GRASS || (under.getType()==Material.SAND && sand)));
    }

	public static boolean isGrowableBlock(Material m) {
		return growableBlocks.containsKey(m);
	}
	
	public static boolean isSpreadableBlock(Material m) {
		return spreadableBlocks.containsKey(m);
    }

    public static boolean isSpreadablePlantSourceBlock(Material m) {
        return spreadablePlants.containsKey(m);
    }
    
    public static Material getSpreadableMaterial(Material m) {
        return spreadablePlants.get(m);
    }
	
	public static boolean isSapling(Material m) {
		return (betterTrees && (m == Material.SAPLING));
	}

	public static Material blockForGrowableBlock(Material m) {
		return growableBlocks.get(m);
	}
	
	public static Material blockForSpreadableBlock(Material m) {
		return spreadableBlocks.get(m);
	}
}
