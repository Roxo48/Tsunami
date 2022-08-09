package me.roxo.tsunami;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tsunami extends WaterAbility implements AddonAbility {


    private static double SOURCE_RANGE;

    private static double RANGE;

    private static long COOLDOWN;

    private static long SPEED;
    private List<Location> locations1 = new ArrayList<>();

    private static int TIME;

    private Block sourceBlock;

    private List<TempBlock> tempBlocks;

    private Freeze freeze;

    private int comapss;
    private Block block;
    private State state;
    private int count;
    private Location blockLocOne;
    private Location blockLocTwo;
    private Location blockLocThree;
    private Location location;
    private ArrayList<Location> locations;
    private Permission perm;
    private Listener listener;
    private int a;

    public Tsunami(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        if (!player.isOnline()) {
            remove();
            return;
        }
        if (CoreAbility.hasAbility(player, this.getClass())) {
            return;
        }
        if (!bPlayer.canBendIgnoreBinds(this)) {
            return;
        }
        if (bPlayer.isOnCooldown(this)) {
            remove();
            return;
        }
        freeze = new Freeze(this, player, 3000);
        tempBlocks = new ArrayList<>();
        location = player.getLocation();
        comapss = 0;
        sourceBlock = GeneralMethods.getTargetedLocation(player, 10).getBlock();
        state = State.SOURCE;

        count = 0;
        a = 0;
        setFields();

        Tsunami eruption = getAbility(player, getClass());
        locations = new ArrayList<>();
        if (eruption != null){
            bPlayer.addCooldown(this);
            eruption.remove();
        }
        this.start();
    }

    private void setFields() {
        SOURCE_RANGE = ConfigManager.getConfig().getDouble("Tsunami.SOURCE_RANGE");
        RANGE = ConfigManager.getConfig().getDouble("Tsunami.RANGE");
        TIME = ConfigManager.getConfig().getInt("Tsunami.TIME");
        COOLDOWN = ConfigManager.getConfig().getLong("Tsunami.COOLDOWN");
        SPEED = ConfigManager.getConfig().getLong("Tsunami.SPEED");
    }

    public void effect(final Location loc) {
        ParticleEffect.ASH.display(loc, 35, 0, .4, 0);
    }

    @Override
    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!Objects.equals(bPlayer.getBoundAbilityName(), "Tsunami")) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }

        block = getWaterSourceBlock(player, SOURCE_RANGE, true);
        if (block == null) return;

        if (!CoreAbility.hasAbility(player, this.getClass())) {return;}
        if (bPlayer.isOnCooldown(this)){ remove(); return;}

        switch (state) {
            case SOURCE:

                progressSource();
                Bukkit.getServer().broadcastMessage("source");
                break;

            case BUILDWATER:

               progressBuild();
                break;

            case DONE:
                Bukkit.getServer().broadcastMessage("Done");
                if (count == 0) {
                    player.sendMessage(ChatColor.RED + " You feel the molten rock heating up awaiting to rise and erupt...");
                    freeze.runTaskTimer(this.getElement().getPlugin(), 0, 20);

                count++;
                break;

            }
        }

    }

    private void progressBuild() {
        player.getEyeLocation().getYaw();
        Bukkit.getServer().broadcastMessage("Build");

//        if(rpGetPlayerDirection(player) == 1){//west 0
//            comapss=1;
//            blockLocOne = location.clone().add(8,0,0);
//            for(int i = -6; i < 6; i++){
//                Location location1 =  location.clone().add(8,0,i);
//                locations.add(location1);
//            }
//        }else if(rpGetPlayerDirection(player) == 2){//north 1
//            comapss = 2;
//            blockLocOne = location.clone().add(0,0,8);
//            for(int i = -6; i < 6; i++){
//                Location location1 =  location.clone().add(i,0,8);
//                locations.add(location1);
//            }
//        }else if (rpGetPlayerDirection(player) == 3){//east 2
//            comapss = 3;
//            blockLocOne = location.clone().add(-8,0,0);
//            for(int i = -6; i < 6; i++){
//                Location location1 =  location.clone().add(-8,0,i);
//                locations.add(location1);
//            }
//        }else if(rpGetPlayerDirection(player) == 0){//south 3
//            comapss = 4;
          //  blockLocOne = location.clone().add(0,0,-8);
//            for(int i = -6; i < 6; i++){
//                Location location1 =  location.clone().add(i,0,-8);
//                locations.add(location1);
//            }
//        }

        locations.add(blockLocOne);
        if(rpGetPlayerDirection(player) == 1){//west 0

            for(int i = -6; i < 6; i++){
                blockLocOne = location.clone().add(8,0,i);
                blockLocTwo = location.clone().add(5,10,i);
                blockLocThree = location.clone().add(3,5,i);
                locations1.addAll(getLocationBezier(blockLocOne, blockLocTwo ,blockLocThree,100));
            }

        }else if(rpGetPlayerDirection(player) == 2){//north 1

            for(int i = -6; i < 6; i++){
                blockLocOne = location.clone().add(i,0,8);
                blockLocTwo = location.clone().add(i,10,5);
                blockLocThree = location.clone().add(i,5,3);
                locations1.addAll(getLocationBezier(blockLocOne, blockLocTwo ,blockLocThree,100));
            }

        }else if (rpGetPlayerDirection(player) == 3){//east 2


            for(int i = -6; i < 6; i++){
                blockLocOne = location.clone().add(-8,0,i);
                blockLocTwo = location.clone().add(-5,10,i);
                blockLocThree = location.clone().add(-3,5,i);
                locations1.addAll(getLocationBezier(blockLocOne, blockLocTwo ,blockLocThree,100));
            }

        }else if(rpGetPlayerDirection(player) == 0){//south 3
            for(int i = -6; i < 6; i++){
                blockLocOne = location.clone().add(i,0,-8);
                blockLocTwo = location.clone().add(i,10,-5);
                blockLocThree = location.clone().add(i,5,-3);
                locations1.addAll(getLocationBezier(blockLocOne, blockLocTwo ,blockLocThree,100));
            }
        }
        //List<Block> blockListOne = new ArrayList<>(getBlocksOFVolcano(blockLocOne));
        List<Block> blockList = new ArrayList<>();
        for(Location location1 : locations1){
            blockList.add(location1.getBlock());
        }
        playWaterbendingSound(player.getLocation());
        for (int i = tempBlocks.size(); i < blockList.size(); i++) {
            Block blockonvoc = blockList.get(i);
            if (GeneralMethods.isSolid(blockonvoc)) {
                if (TempBlock.isTempBlock(blockonvoc)) {
                    TempBlock tb = TempBlock.get(blockonvoc);
                    if (!tempBlocks.contains(tb)) {
                        state = State.DONE;
                        return;
                    }
                } else if (blockonvoc != sourceBlock) {
                }
            }
            TempBlock tempBlock = new TempBlock(blockonvoc, Material.BROWN_CONCRETE);
            tempBlocks.add(tempBlock);
            tempBlock.setRevertTime(15000);
        }
        state = State.DONE;
    }


    public void onShift() {
        if(state == State.SOURCE){
            state = State.BUILDWATER;
        }
    }

    public void progressShoot() {
//       // Location locationOfPlayer = GeneralMethods.getTargetedLocation(player, 20);
//        if(rpGetPlayerDirection(player) == 1){//west 0
//            blockLocOne = location.clone().add(8,0,0);
//            blockLocTwo = location.clone().add(5,10,0);
//            blockLocThree = location.clone().add(3,5,0);
//
//        }else if(rpGetPlayerDirection(player) == 2){//north 1
//            blockLocOne = location.clone().add(0,0,8);
//            blockLocTwo = location.clone().add(0,10,5);
//            blockLocThree = location.clone().add(0,5,3);
//
//        }else if (rpGetPlayerDirection(player) == 3){//east 2
//
//            blockLocOne = location.clone().add(-8,0,0);
//            blockLocTwo = location.clone().add(-5,10,0);
//            blockLocThree = location.clone().add(-3,5,0);
//
//        }else if(rpGetPlayerDirection(player) == 0){//south 3
//            blockLocOne = location.clone().add(0,0,-8);
//            blockLocTwo = location.clone().add(0,10,-5);
//            blockLocThree = location.clone().add(0,5,-3);
//        }
//        List<Location> locations1 = getLocationBezier(blockLocOne, blockLocTwo ,blockLocThree,100);
////        List<Location> locations2 = getLocationBezier();
////        List<Location> locations3 = getLocationBezier();
////        List<Location> locations4 = getLocationBezier();
////        List<Location> locations5 = getLocationBezier();
////        List<Location> locations6 = getLocationBezier();
////        List<Location> locations7 = getLocationBezier();
////        List<Location> locations8 = getLocationBezier();
////        List<Location> locations9 = getLocationBezier();
////        List<Location> locations10 = getLocationBezier();
////        List<Location> locations11= getLocationBezier();
////        List<Location> locations12 = getLocationBezier();
////initial

        BukkitRunnable br = new BukkitRunnable() {
            @Override
            public void run() {
                if(a >= 15){
                    cancel();
                }
                if(rpGetPlayerDirection(player) == 1){//west 0
                    //x
                        for(Location location1 : locations1){
                            location1.clone().add(a,0,0);
                        }
                }else if(rpGetPlayerDirection(player) == 2){//north 1
                    //z
                    for(Location location1 : locations1){
                        location1.clone().add(0,0,a);
                    }
                }else if (rpGetPlayerDirection(player) == 3){//east 2
                    //-x
                    for(Location location1 : locations1){
                        location1.clone().add(-a,0,0);
                    }
                }else if(rpGetPlayerDirection(player) == 0){//south 3
                    //-z
                    for(Location location1 : locations1){
                        location1.clone().add(0,0,-a);
                    }
                }
                tempBlocks.clear();
                List<Block> blockList = new ArrayList<>();
                for(Location location1 : locations1){
                    blockList.add(location1.getBlock());
                }
                playWaterbendingSound(player.getLocation());
                for (int i = tempBlocks.size(); i < blockList.size(); i++) {
                    Block blockonvoc = blockList.get(i);
                    if (GeneralMethods.isSolid(blockonvoc)) {
                        if (TempBlock.isTempBlock(blockonvoc)) {
                            TempBlock tb = TempBlock.get(blockonvoc);
                            if (!tempBlocks.contains(tb)) {
                                state = State.NULL;
                                return;
                            }
                        } else if (blockonvoc != sourceBlock) {
                        }
                    }
                    TempBlock tempBlock = new TempBlock(blockonvoc, Material.BLACK_GLAZED_TERRACOTTA);
                    tempBlocks.add(tempBlock);
                    tempBlock.setRevertTime(2000);
                }
                a++;
            }
        };
        br.runTaskTimer(ProjectKorra.plugin, 20* 60, 0);

    }

    private void progressSource() {
        Bukkit.getServer().broadcastMessage("source");
        effect(sourceBlock.getLocation());
        Location location1 = sourceBlock.getLocation();
        for(int i = -1; i < 1; i++){
            for(int j = -1; j < 1; j++){
                Location location2 = location1.clone().add(i,0,j);
                if(isWaterbendable(location2.getBlock() )|| isPlantbendable(location2.getBlock())){
                    remove();
                }
            }
        }
        if (sourceBlock.getLocation().distanceSquared(player.getLocation()) > SOURCE_RANGE * SOURCE_RANGE || (!isWaterbendable( sourceBlock) && !isPlantbendable(sourceBlock)) ){
            remove();
        }


    }
    public List<Location> getLocationBezier(Location p0,Location p1,Location p2,float t){
        List<Location> points = new ArrayList<>();
        for(int i = 0; i <= t; i ++){
            float a = i / t;
            points.add(bezierPoint(a,p0,p1,p2));
        }
        return points;
    }
    public Location bezierPoint(float t, Location p0, Location p1, Location p2){
        // pFinal[0] = Math.pow(1 - t, 2) * p0[0] + (1-t) * 2 * t * p1[0] + t * t * p2[0];
        // pFinal[1] = Math.pow(1 - t, 2) * p0[1] + (1-t) * 2 * t * p1[1] + t * t * p2[1];
        return p0.clone().multiply((1-t)*(1-t)).add(p1.clone().multiply((1-t) * 2 * t)).add(p2.clone().multiply(t*t));
    }

    public int rpGetPlayerDirection(Player playerSelf){
        int dir = 0;
        float y = playerSelf.getLocation().getYaw();
        if( y < 0 ){y += 360;}
        y %= 360;
        int i = (int)((y+8) / 22.5);
        if(i == 0){dir = 0;}
        //west
        else if(i == 1){dir = 0;}
        else if(i == 2){dir = 0;}
        //north
        else if(i == 3){dir = 1;}
        else if(i == 4){dir = 1;}
        else if(i == 5){dir = 1;}
        else if(i == 6){dir = 1;}
        //east
        else if(i == 7){dir = 2;}
        else if(i == 8){dir = 2;}
        else if(i == 9){dir = 2;}
        else if(i == 10){dir = 2;}
        //south
        else if(i == 11){dir = 3;}
        else if(i == 12){dir = 3;}
        else if(i == 13){dir = 3;}
        else if(i == 14){dir = 3;}

        else if(i == 15){dir = 0;}
        else {dir = 0;}
        return dir;
    }
    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 1000;
    }

    @Override
    public String getName() {
        return "Tsunami";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void load() {
        perm = new Permission("bending.ability.tsunami");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
        listener = new Listener();
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);
        //perm.setDefault(PermissionDefault.OP);
        final FileConfiguration config = ConfigManager.defaultConfig.get();
        config.addDefault("Tsunami.SOURCE_RANGE",(Object) 8);
        config.addDefault("Tsunami.RANGE",(Object) 15);
        config.addDefault("Tsunami.COOLDOWN",(Object) 12000);
        config.addDefault("Tsunami.TIME", (Object) 5);
        config.addDefault("Tsunami.SPEED", (Object) 4);
        ConfigManager.defaultConfig.save();
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(listener);
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
    }

    @Override
    public String getDescription(){
        return "\"Elements Of The Avatar Addons:\"\n" +
                "\"With this Combo, this create a strong wave of water that push players in away and near water players Knockback, this can even make you move faster." +
                "\nLeft Click on a water source 3x3 and then Shift to activate.";
    }

    @Override
    public String getAuthor() {
        return "Roxo";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }



}
