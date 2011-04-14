package com.smartaleq.bukkit.dwarfcraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.entity.Vehicle;

import redecouverte.npcspawner.NpcSpawner;

final class DataManager {

	private List<DCPlayer> dwarves = new ArrayList<DCPlayer>();
	private List<DwarfVehicle> vehicleList = new ArrayList<DwarfVehicle>();
	public HashMap<String, DwarfTrainer> trainerList = new HashMap<String, DwarfTrainer>();
	private HashMap<String, GreeterMessage> greeterMessageList = new HashMap<String, GreeterMessage>();
	private final ConfigManager configManager;
	private final DwarfCraft plugin;
	private List<Player> trainerRemove = new ArrayList<Player>();

	protected DataManager(DwarfCraft plugin, ConfigManager cm) {
		this.plugin = plugin;
		this.configManager = cm;
		dbInitialize();
		for (Iterator<World> i = plugin.getServer().getWorlds().iterator(); i
				.hasNext();) {
			World w = i.next();
			populateTrainers(w);
		}
	}

	protected void addVehicle(DwarfVehicle v) {
		vehicleList.add(v);
	}

	/**
	 * this is untested and quite a lot of new code, it will probably fail
	 * several times. no way to bugfix currently. Just praying it works
	 * 
	 * @param oldVersion
	 */
	private void buildDB(int oldVersion) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			Statement statement = conn.createStatement();
			
			ResultSet rs  = statement.executeQuery("select * from sqlite_master WHERE name = 'trainers';");
			// SCHEMA(world,uniqueId,name,skill,maxSkill,material,isGreeter,messageId,x,y,z,yaw,pitch)
			if (!rs.next()){
				statement.executeUpdate(
						"create table trainers " +
						"  (" +
						"    world, uniqueId, name, skill, maxSkill, material, isGreeter, messageId, " +
						"    x, y, z, yaw, pitch" +
						"  );");
			}
			rs.close();

			rs = statement.executeQuery("select * from sqlite_master WHERE name = 'players';");
			if (!rs.next()){
				statement.executeUpdate("create table players ( id INTEGER PRIMARY KEY, name, race );");
			}
			rs.close();

			rs = statement.executeQuery("select * from sqlite_master WHERE name = 'skills';");
			if (!rs.next()){
				statement.executeUpdate(
						"CREATE TABLE 'skills' " +
						"  ( " +
						"    'player' INT, " +
						"    'id' int, " +
						"    'level' INT DEFAULT 0, " +
						"    PRIMARY KEY ('player','id') " +
						"  );"
				);
			}
			rs.close();
			
			rs = statement.executeQuery("select name from sqlite_master WHERE name LIKE 'dwarf%';");
			while(rs.next()){
				convertOld(conn, rs.getString("name"));
				statement.execute("DROP TABLE " + rs.getString("name") + ";");
			}
			
			conn.close();
		} catch (SQLException e) {
			System.out.println("[SEVERE]DB not built successfully");
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void convertOld(Connection conn, String name) throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + name + ";" );
		System.out.println("DC Init: Converting old table: " + name + " (may lag a little wait for complete message)");

		conn.setAutoCommit(false);
		while(rs.next()){
			String playerName = rs.getString("playername");
			int id = getPlayerID(conn, playerName);
			
			if(id == -1){
				PreparedStatement prep = conn.prepareStatement("insert into players(name, race) values(?,?);" );
				prep.setString(1, playerName);
				prep.setString(2, (rs.getBoolean("iself") ? "Elf" : "Dwarf"));
				prep.execute();
				prep.close();
				id = getPlayerID(conn, playerName);
			}			

			PreparedStatement prep = conn.prepareStatement("INSERT INTO skills(player, id, level) values(?,?,?);");			
			HashMap<Integer,Skill> skills = plugin.getConfigManager().getAllSkills();
			for(Skill skill : skills.values()){
				
				prep.setInt(1, id);
				prep.setInt(2, skill.getId());
				try{
					prep.setInt(3, rs.getInt(skill.toString()));
				}catch(SQLException e){
					prep.setInt(3, 0);
				}
				prep.addBatch();
			}
			prep.executeBatch();
			prep.close();			
		}
		conn.setAutoCommit(true);
		System.out.println("DC Init: Converting of " + name + " complete");		
	}

	protected boolean checkTrainersInChunk(Chunk chunk) {
		for (Iterator<Map.Entry<String, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, DwarfTrainer> pairs = i.next();
			DwarfTrainer d = (pairs.getValue());
			if (Math.abs(chunk.getX() - d.getLocation().getBlock().getChunk().getX()) > 1)
				continue;
			if (Math.abs(chunk.getZ() - d.getLocation().getBlock().getChunk().getZ()) > 1)
				continue;
			return true;
		}
		return false;
	}

	protected DCPlayer createDwarf(Player player) {
		DCPlayer newDwarf = new DCPlayer(plugin, player);
		newDwarf.changeRace(plugin.getConfigManager().getDefaultRace());
		newDwarf.setSkills(plugin.getConfigManager().getAllSkills(newDwarf.getRace()));
		for (Skill skill : newDwarf.getSkills().values())
			skill.setLevel(0);
		if (player != null)
			dwarves.add(newDwarf);
		return newDwarf;
	}

	protected void createDwarfData(DCPlayer dCPlayer) {
		createDwarfData(dCPlayer.getPlayer().getName(), dCPlayer.isElf());
	}
	protected void createDwarfData(String name, boolean isElf) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());

			PreparedStatement prep = conn.prepareStatement("insert into players(name, race) values(?,?);" );
			prep.setString(1, name);
			prep.setString(2, isElf ? "Elf" : "Dwarf");
			prep.execute();
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dbInitialize() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			Statement statement = conn.createStatement();
			ResultSet rs;
			rs = statement.executeQuery("select * from sqlite_master WHERE name = 'players';");
			if (!rs.next()){
				/*for (int versionNumber = configManager.getConfigSkillsVersion() - 1; versionNumber >= 100; versionNumber--) { 
					rs = statement.executeQuery("select * from sqlite_master WHERE name = 'dwarfs" + versionNumber + "';");
					if (rs.next()) { // if there is a recent past tabl,
											// use it to build the new table
						conn.close();
						buildDB(versionNumber);
						return;
					}
				}
				conn.close();
				buildDB(0); // if there are no recent past tables, build a new
							// db from scratch
				* 
			 	*/
				buildDB(0);
			}
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds a dwarf from the server's static list based on player's name
	 * 
	 * @param player
	 * @return dwarf or null
	 */
	protected DCPlayer find(Player player) {
		for (DCPlayer d : plugin.getDataManager().getDwarves()) {
			if (d != null){
				if (d.getPlayer() != null){
					if (d.getPlayer().getName().equalsIgnoreCase(player.getName())){
						d.setPlayer(player);
						return d;	
					}
				}
			}			
		}
		return null;
	}
	
	protected DCPlayer findOffline(String name) {
		DCPlayer dCPlayer = createDwarf(null);
		if (getDwarfData(dCPlayer, name))
			return dCPlayer;
		else {
			// No dwarf or data found
			return null;
		}
	}

	protected boolean getDwarfData(DCPlayer player) {
		return getDwarfData(player, player.getPlayer().getName());
	}

	/**
	 * Used for creating and populating a dwarf with a null(offline) player
	 * 
	 * @param dCPlayer
	 * @param name
	 */
	private boolean getDwarfData(DCPlayer dCPlayer, String name) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			
			PreparedStatement prep = conn.prepareStatement("select * from players WHERE name = ?;");
			prep.setString(1, name);
			ResultSet rs = prep.executeQuery();
			
			if (!rs.next())
				return false;
			
			System.out.println("DC: PlayerJoin success for " + dCPlayer.getPlayer().getName());
			
			dCPlayer.changeRace(plugin.getConfigManager().findRace(rs.getString("race"), false));
			int id = rs.getInt("id");
			rs.close();
			
			prep.close();
			prep = conn.prepareStatement("select id, level from skills WHERE player = ?;");
			prep.setInt(1, id);
			
			while(rs.next()){
				int skillID = rs.getInt("id");
				int level = rs.getInt("level");
				Skill skill = dCPlayer.getSkill(skillID);
				if (skill != null)
					skill.setLevel(level);
			}
			rs.close();
			conn.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Deprecated
	// TODO: remove this and replace by other stuff
	protected List<DCPlayer> getDwarves() {
		return dwarves;
	}

	protected GreeterMessage getGreeterMessage(String messageId) {
		System.out.println(messageId);
		return greeterMessageList.get(messageId);
	}

	protected DwarfTrainer getTrainer(Entity entity) {
		// kind of ugly, could replace this with a hashmap, but i dont think the
		// perf. gains will be very significant
		for (Iterator<Map.Entry<String, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, DwarfTrainer> pairs = i.next();
			DwarfTrainer d = (pairs.getValue());
			if (d.getBasicHumanNpc().getBukkitEntity().getEntityId() == entity.getEntityId())
				return d;
		}
		return null;
	}

	protected DwarfTrainer getTrainer(String str) {
		return (trainerList.get(str)); // can return null
	}

	protected DwarfVehicle getVehicle(Vehicle v) {
		for (DwarfVehicle i : vehicleList) {
			if (i.equals(v)) {
				return i;
			}
		}
		return null;
	}

	protected void insertGreeterMessage(String messageId,
			GreeterMessage greeterMessage) {
		try {
			greeterMessageList.put(messageId, greeterMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void insertTrainer(DwarfTrainer d) {
		assert (d != null);
		trainerList.put(d.getUniqueId(), d);
		// SCHEMA(world,uniqueId,name,skill,maxSkill,material,isGreeter,messageId,x,y,z,yaw,pitch)
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			PreparedStatement prep = conn.prepareStatement(
					"insert into trainers values (?,?,?,?,?,?,?,?,?,?,?,?,?);"
					);
			prep.setString(1, d.getWorld().getName());
			prep.setString(2, d.getUniqueId());
			prep.setString(3, d.getName());
			if (!d.isGreeter()) {
				prep.setInt(4, d.getSkillTrained());
				prep.setInt(5, d.getMaxSkill());
			} else {
				prep.setInt(4, 0);
				prep.setInt(5, 0);
			}
			prep.setInt    (6,  d.getMaterial());
			prep.setBoolean(7,  d.isGreeter());
			prep.setString (8,  d.getMessage());
			prep.setDouble (9,  d.getLocation().getX());
			prep.setDouble (10, d.getLocation().getY());
			prep.setDouble (11, d.getLocation().getZ());
			prep.setFloat  (12, d.getLocation().getYaw());
			prep.setFloat  (13, d.getLocation().getPitch());
			
			if (DwarfCraft.debugMessagesThreshold < 7)
				System.out.println("Debug Message Added trainer"
						+ d.getUniqueId() + " in world: "
						+ d.getWorld().getName());
			
			prep.addBatch();
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	private HashMap<String, DwarfTrainer> populateTrainers(World world) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"
					+ configManager.getDbPath());
			Statement statement = conn.createStatement();
			String query = "select * from trainers Where world='"
					+ world.getName() + "';";
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				// DB SCHEMA
				// (world,uniqueId,name,skill,maxSkill,material,isGreeter,messageId,x,y,z,yaw,pitch)
				if (world.getName().equals(rs.getString("world"))) {
					// create trainer in this world
					// if (DwarfCraft.debugMessagesThreshold < 7)
					if (DwarfCraft.debugMessagesThreshold < 5)
						System.out.println("DC5: trainer:"
								+ rs.getString("name") + " in world: "
								+ world.getName());
					DwarfTrainer trainer = new DwarfTrainer(plugin, world,
							rs.getString("uniqueId"), 
							rs.getString("name"),
							rs.getInt("skill"), 
							rs.getInt("maxSkill"),
							Material.getMaterial(rs.getInt("material")),
							rs.getBoolean("isGreeter"),
							rs.getString("messageId"), 
							rs.getDouble("x"),
							rs.getDouble("y"), 
							rs.getDouble("z"),
							rs.getFloat("yaw"), 
							rs.getFloat("pitch"));
					trainerList.put(rs.getString("uniqueId"), trainer);
				}
			}
			rs.close();
			statement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trainerList;
	}


	protected void removeTrainer(String str) {
		DwarfTrainer d;
		d = trainerList.remove(str);
		NpcSpawner.RemoveBasicHumanNpc(d.getBasicHumanNpc());

		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+ configManager.getDbPath());
			Statement statement = conn.createStatement();
			statement.execute("delete from trainers where uniqueId='" + d.getUniqueId() + "';");
			statement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void removeVehicle(Vehicle v) {
		for (DwarfVehicle i : vehicleList) {
			if (i.equals(v)) {
				plugin.getDataManager().vehicleList.remove(i);
				if (DwarfCraft.debugMessagesThreshold < 5)
					System.out.println("DC5:Removed DwarfVehicle from vehicleList");
			}
		}
	}

	private int getPlayerID(String name) {
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			int id = getPlayerID(conn, name);
			conn.close();
			return id;
		}catch(Exception e ){
			System.out.println("DC: Failed to get player ID: " + name);			
		}
		return -1;
	}
	private int getPlayerID(Connection conn, String name) {
		try{
		
			PreparedStatement prep = conn.prepareStatement("select id from players WHERE name = ?;");
			prep.setString(1, name);
			ResultSet rs = prep.executeQuery();
		
			if (!rs.next())
				return -1;		
		
			int id = rs.getInt("id");
			rs.close();
			return id;
		}catch(Exception e ){
			System.out.println("DC: Failed to get player ID: " + name);
		}
		return -1;
	}

	protected boolean saveDwarfData(DCPlayer dCPlayer) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + configManager.getDbPath());
			
			PreparedStatement prep = conn.prepareStatement("UPDATE players SET race=? WHERE name=?;");
			prep.setString(1, dCPlayer.getPlayer().getName());
			prep.execute();
			prep.close();
			
			prep = conn.prepareStatement("REPLACE INTO skills(player, id, level) values(?,?,?);");
			
			int id = getPlayerID(dCPlayer.getPlayer().getName());
			for (Skill skill : dCPlayer.getSkills().values()){
				prep.setInt(1, id);
				prep.setInt(2, skill.getId());
				prep.setInt(3, skill.getLevel());
				prep.addBatch();
			}
			prep.executeBatch();
			conn.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setTrainerRemove(List<Player> trainerRemove) {
		this.trainerRemove = trainerRemove;
	}

	public List<Player> getTrainerRemove() {
		return trainerRemove;
	}

}
