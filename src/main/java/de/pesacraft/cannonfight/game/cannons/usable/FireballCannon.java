package de.pesacraft.cannonfight.game.cannons.usable;

import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mongodb.client.MongoCollection;

import de.pesacraft.cannonfight.CannonFight;
import de.pesacraft.cannonfight.data.players.CannonFighter;
import de.pesacraft.cannonfight.game.cannons.Cannon;
import de.pesacraft.cannonfight.util.Collection;
import de.pesacraft.cannonfight.util.MongoDatabase;
import de.pesacraft.cannonfight.util.Upgrade;

@SuppressWarnings("unchecked")
public class FireballCannon extends Cannon {

	private static final MongoCollection<Document> COLLECTION;
	
	/**
	 * ItemStack representing the cannon ingame
	 */
	protected static final ItemStack ITEM;
	/**
	 * The cannons name
	 */
	protected static final String NAME = "FireballCannon";
	
	protected static final Map<Integer, Upgrade<Integer>> COOLDOWN_MAP = new HashMap<Integer, Upgrade<Integer>>();
	
	protected static final Map<Integer, Upgrade<Integer>> AMMO_MAP = new HashMap<Integer, Upgrade<Integer>>();
	
	protected static final Map<Integer, Upgrade<Double>> RADIUS_MAP = new HashMap<Integer, Upgrade<Double>>();
	
	protected static final Map<Integer, Upgrade<Double>> DAMAGE_MAP = new HashMap<Integer, Upgrade<Double>>();
	
	static {
		COLLECTION = Collection.ITEMS();
		
		Document doc = COLLECTION.find(eq("name", NAME)).first();
		
		if (doc != null) {
			// Cannon in database
			ITEM = ItemStack.deserialize((Document) doc.get("item"));
			
			Document cooldownList = (Document) doc.get("cooldown");
			for (Entry<String, Object> cooldown : cooldownList.entrySet())
				COOLDOWN_MAP.put(Integer.parseInt(cooldown.getKey()), new Upgrade<Integer>((Document) cooldown.getValue()));
			
			Document ammoList = (Document) doc.get("ammo");
			for (Entry<String, Object> ammo : ammoList.entrySet())
				AMMO_MAP.put(Integer.parseInt(ammo.getKey()), new Upgrade<Integer>((Document) ammo.getValue()));
			
			Document radiusList = (Document) doc.get("radius");
			for (Entry<String, Object> ammo : ammoList.entrySet())
				RADIUS_MAP.put(Integer.parseInt(ammo.getKey()), new Upgrade<Double>((Document) ammo.getValue()));
			
			Document damage = (Document) doc.get("damage");
			for (Entry<String, Object> ammo : ammoList.entrySet())
				DAMAGE_MAP.put(Integer.parseInt(ammo.getKey()), new Upgrade<Double>((Document) ammo.getValue()));
			
		}
		else {
			// Cannon not in database
			ITEM = new ItemStack(Material.FIREBALL);
			
			Map<String, Object> coolMap = new HashMap<String, Object>();
			for (int i = 1; i <= 10; i++) {
				Upgrade<Integer> u = new Upgrade<Integer>(i * 100, 11 - i);
				COOLDOWN_MAP.put(i, u);
				coolMap.put(i + "", u);
			}
			
			Map<String, Object> ammoMap = new HashMap<String, Object>();
			for (int i = 1; i <= 10; i++) {
				Upgrade<Integer> u = new Upgrade<Integer>(i * 100, i);
				AMMO_MAP.put(i, u);
				ammoMap.put(i + "", u);
			}
			
			Map<String, Object> radiusMap = new HashMap<String, Object>();
			for (int i = 1; i <= 10; i++) {
				Upgrade<Double> u = new Upgrade<Double>(i * 100, i * 0.1 + 1);
				RADIUS_MAP.put(i, u);
				radiusMap.put(i + "", u);
			}
			
			Map<String, Object> damageMap = new HashMap<String, Object>();
			for (int i = 1; i <= 10; i++) {
				Upgrade<Double> u = new Upgrade<Double>(i * 100, i * 0.5 + 0.5);
				DAMAGE_MAP.put(i, u);
				damageMap.put(i + "", u);
			}
		
			doc = new Document("name", NAME);
			doc = doc.append("item", new Document(ITEM.serialize()));
			
			doc = doc.append("cooldown", new Document(coolMap));
			doc = doc.append("ammo", new Document(ammoMap));
			doc = doc.append("radius", new Document(radiusMap));
			doc = doc.append("damage", new Document(damageMap));
			
			COLLECTION.insertOne(doc);	
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	private int maxAmmo;
	private int currentAmmo;
	private double radius;
	private double damage;
	private ItemStack item;
	
	public FireballCannon(int levelAmmo, int levelCooldown, int levelRadius, int levelDamage) throws SQLException {
		super(COOLDOWN_MAP.get(levelCooldown).getValue());
	
		currentAmmo = maxAmmo = AMMO_MAP.get(levelAmmo).getValue();
			
		radius = RADIUS_MAP.get(levelRadius).getValue();
		damage = DAMAGE_MAP.get(levelDamage).getValue();
	
		item = ITEM.clone();
	}

	@Override
	public ItemStack getItem() {
		return item;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean fire(CannonFighter fighter) {
		if (!hasFinished())
			return false;
		
		if (--currentAmmo == 0)
			start();
		else
			item.setAmount(currentAmmo);
		
		Player p = fighter.getPlayer();
		
		Block b = p.getLocation().getBlock().getRelative(0, -1, 0);
		
		Material m = b.getType();
		
		b.setType(Material.SKULL);
		Skull skull = (Skull) b.getState();
		skull.setSkullType(SkullType.PLAYER);
		skull.setOwner("FroznMine");
		skull.update(true);

		FallingBlock fb = p.getWorld().spawnFallingBlock(p.getLocation(), Material.SKULL, b.getData());
	
		b.setType(m);
		fb.setVelocity(new Vector(0, 50, 0));
		
		return true;
	}

	@Override
	public int getMaxAmmo() {
		return maxAmmo;
	}

	@Override
	public boolean hasAmmo() {
		return currentAmmo > 0;
	}

	@Override
	protected void update() {
		item.setDurability((short) (item.getType().getMaxDurability() * done()));
	}

	@Override
	protected void finished() {
		item.setDurability(item.getType().getMaxDurability());
	}
}