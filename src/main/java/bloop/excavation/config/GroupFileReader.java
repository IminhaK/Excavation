package bloop.excavation.config;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.util.*;

public class GroupFileReader {

    public static final Map<String, Set<Block>> groups = readIt();

    private static Map<String, Set<Block>> readIt() {
        Gson gson = new Gson();
        File configFile = new File(FMLPaths.CONFIGDIR.get().toString(), "excavation-groups.json");
        List<List<String>> newGroups = new ArrayList<>();
        newGroups.add( new ArrayList<>());

        if(configFile.exists()) {
            try(Reader reader = new FileReader(configFile.getAbsolutePath())) {
                newGroups = gson.fromJson(reader, newGroups.getClass());
                System.out.println(newGroups);
            } catch(IOException e) {
                System.out.println("[Excavation] Error reading groups config. Ignoring it.");
                e.printStackTrace();
                return new HashMap<>(); //empty
            }
        } else {
            System.out.println("[Excavation] Groups config does not exist. Creating it.");
            try(FileWriter writer = new FileWriter(configFile.getAbsolutePath())) {
                gson.toJson(newGroups, writer);
            } catch(IOException e) {
                System.out.println("[Excavation] Error creating groups config.");
                e.printStackTrace();
                return new HashMap<>(); //empty
            }
        }

        return setupNewGroups(newGroups);
    }

    private static Map<String, Set<Block>> setupNewGroups(List<List<String>> stringGroups) {
        Map<String, Set<Block>> setupGroups = new HashMap<>();
        Set<Block> newBlockSet = new HashSet<>();

        for(List<String> list : stringGroups) {
            newBlockSet.clear();
            for(String str : list) {
                for(String nStr : list)
                    newBlockSet.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nStr)));
                Set<Block> dummyNewBlockSet = new HashSet<>();
                dummyNewBlockSet.addAll(newBlockSet);
                setupGroups.put(str, dummyNewBlockSet);
            }
        }
        return setupGroups;
    }
}
