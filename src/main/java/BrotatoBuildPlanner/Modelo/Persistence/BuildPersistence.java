package BrotatoBuildPlanner.Modelo.Persistence;

import BrotatoBuildPlanner.Modelo.Catalog.GameCatalog;
import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Guardado/carga de build en formato JSON.
 */
public class BuildPersistence {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void save(Path path, Character character, Map<Item, Integer> items, Map<Weapon, Integer> weapons) throws IOException {
        SaveBuild save = new SaveBuild();
        save.character = character != null ? character.getName() : null;
        save.items = new LinkedHashMap<>();
        save.weapons = new LinkedHashMap<>();

        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            save.items.put(entry.getKey().getName(), entry.getValue());
        }

        for (Map.Entry<Weapon, Integer> entry : weapons.entrySet()) {
            save.weapons.put(entry.getKey().getName(), entry.getValue());
        }

        Files.writeString(path, GSON.toJson(save), StandardCharsets.UTF_8);
    }

    public LoadedBuild load(Path path, GameCatalog catalog) throws IOException {
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        SaveBuild save = GSON.fromJson(raw, SaveBuild.class);

        LoadedBuild loaded = new LoadedBuild();
        loaded.character = save.character == null ? null : catalog.findCharacter(save.character);
        loaded.items = new LinkedHashMap<>();
        loaded.weapons = new LinkedHashMap<>();

        if (save.items != null) {
            for (Map.Entry<String, Integer> entry : save.items.entrySet()) {
                Item item = catalog.findItem(entry.getKey());
                if (item != null && entry.getValue() != null && entry.getValue() > 0) {
                    loaded.items.put(item, entry.getValue());
                }
            }
        }

        if (save.weapons != null) {
            for (Map.Entry<String, Integer> entry : save.weapons.entrySet()) {
                Weapon weapon = catalog.findWeapon(entry.getKey());
                if (weapon != null && entry.getValue() != null && entry.getValue() > 0) {
                    loaded.weapons.put(weapon, entry.getValue());
                }
            }
        }

        return loaded;
    }

    private static class SaveBuild {
        String character;
        Map<String, Integer> items;
        Map<String, Integer> weapons;
    }

    public static class LoadedBuild {
        public Character character;
        public Map<Item, Integer> items;
        public Map<Weapon, Integer> weapons;
    }
}
