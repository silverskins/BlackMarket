package dev.minecraftdorado.BlackMarket.Utils.Inventory.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.minecraftdorado.BlackMarket.MainClass.MainClass;
import dev.minecraftdorado.BlackMarket.Utils.Utils;

public class CategoryUtils {
	
	private static HashMap<String, Category> list = new HashMap<>();
	private File f = new File(MainClass.main.getDataFolder() + "/categories");
	
	public CategoryUtils() {
		if(!f.exists() || f.listFiles().length == 0)
			for(String filter : new String[]{"all","ores","tools","wood","potions","redstone"})
				Utils.extract("resources/categories/" + filter + ".yml", "categories/" + filter + ".yml");
		for(File file : f.listFiles()) {
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			
			String key = file.getName().replace(".yml", "");
			Category filter = new Category(key);
			
			if(yml.isSet("row"))
				filter.setRow(yml.getInt("row"));
			
			if(yml.isSet("materials"))
				yml.getStringList("materials").forEach(mat -> filter.addMaterial(UMaterial.match(mat)));
			
			if(yml.isSet("name"))
				filter.setName(yml.getString("name"));
			
			list.put(key, filter);
		}
	}
	
	public static Category addCategory(Category category) {
		list.put(category.getName(), category);
		return category;
	}
	
	public static ArrayList<String> getKeys(){
		ArrayList<String> l = new ArrayList<>();
		list.keySet().forEach(k -> l.add(k));
		return l;
	}
	
	public static Collection<Category> getCategories(){
		return list.values();
	}
	
	public class Category {
		
		private String key;
		private String name;
		private int row;
		
		private ArrayList<UMaterial> mats = new ArrayList<>();
		private ItemStack item;
		
		public Category(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setRow(int row) {
			this.row = row < 7 && row > 0 ? row : 1;
		}
		
		public int getRow() {
			return row;
		}
		
		public ItemStack getItemStack(Boolean status) {
			if(item != null)
				return item;
			ItemStack item = Utils.getItemStack(new File(f.getAbsolutePath() + "/" + name + ".yml"), "item");
			ItemMeta meta = item.getItemMeta();
			
			if(status)
				meta.addEnchant(Enchantment.DURABILITY, 1, false);
			
			if(meta.hasDisplayName())
				if(meta.getDisplayName().contains("%category_name%")) meta.setDisplayName(meta.getDisplayName().replace("%category_name%", name));
			
			item.setItemMeta(meta);
			return item;
		}
		
		public void addMaterial(UMaterial uMat) {
			if(!this.mats.contains(uMat))
				this.mats.add(uMat);
		}
		
		public ArrayList<UMaterial> getMaterials(){
			return mats;
		}
		
		public boolean contain(UMaterial uMat) {
			return mats.contains(uMat);
		}
	}
}
