package BrotatoBuildPlanner.Modelo;

import BrotatoBuildPlanner.Modelo.Catalog.GameCatalog;
import BrotatoBuildPlanner.Modelo.Effects.StartEffect;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Persistence.BuildPersistence;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Stats.Stats;
import BrotatoBuildPlanner.Modelo.Weapon.ComputedWeaponStats;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Orquesta la build activa: seleccion, limites, calculo y persistencia.
 */
public class BuildManager {
    private final GameCatalog catalog;
    private final BuildCalculator calculator;
    private final BuildPersistence persistence;

    private Character selectedCharacter;
    private final LinkedHashMap<Item, Integer> selectedItems;
    private final LinkedHashMap<Weapon, Integer> selectedWeapons;

    public BuildManager(GameCatalog catalog) {
        this.catalog = catalog;
        this.calculator = new BuildCalculator();
        this.persistence = new BuildPersistence();
        this.selectedItems = new LinkedHashMap<>();
        this.selectedWeapons = new LinkedHashMap<>();
    }

    public boolean hasBuildProgress() {
        return selectedCharacter != null || !selectedItems.isEmpty() || !selectedWeapons.isEmpty();
    }

    public void selectCharacter(Character character) {
        resetBuild();
        selectedCharacter = character;
        applyCharacterStartEffects();
    }

    public boolean addItem(Item item) {
        int current = selectedItems.getOrDefault(item, 0);
        int max = Math.max(1, item.getMaxStack());
        if (current >= max) {
            return false;
        }
        selectedItems.put(item, current + 1);
        return true;
    }

    public boolean addWeapon(Weapon weapon) {
        if (selectedCharacter == null) {
            return false;
        }

        int totalWeapons = getTotalSelectedWeapons();
        if (totalWeapons >= selectedCharacter.getMaxWeaponSlots()) {
            return false;
        }

        int current = selectedWeapons.getOrDefault(weapon, 0);
        int max = Math.max(1, weapon.getCuantity());
        if (current >= max) {
            return false;
        }

        selectedWeapons.put(weapon, current + 1);
        return true;
    }

    public void resetBuild() {
        selectedCharacter = null;
        selectedItems.clear();
        selectedWeapons.clear();
    }

    public Stats calculateBuildStats() {
        BuildContext context = new BuildContext(
                selectedCharacter,
            flattenItems(selectedItems),
            flattenWeapons(selectedWeapons)
        );

        if (selectedCharacter == null) {
            return new Stats();
        }

        return calculator.calculate(context, catalog.getWeaponSetBonuses());
    }

    public ComputedWeaponStats calculateWeaponStats(Weapon weapon, Stats buildStats) {
        double damageBonus = getDamageBonusByType(weapon, buildStats);
        double attackSpeedPct = buildStats.getStat(Stat.ATTACK_SPEED);
        double range = weapon.getRange() + buildStats.getStat(Stat.RANGE);
        double lifeSteal = weapon.getLifesteal() + buildStats.getStat(Stat.LIFE_STEAL);

        double finalDamage = weapon.getDamage() * (1 + (damageBonus / 100.0));
        double finalAttackSpeed = weapon.getAttackSpeed() * Math.max(0.1, 1 + (attackSpeedPct / 100.0));

        return new ComputedWeaponStats(finalDamage, finalAttackSpeed, range, lifeSteal);
    }

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public Map<Item, Integer> getSelectedItems() {
        return Collections.unmodifiableMap(selectedItems);
    }

    public Map<Weapon, Integer> getSelectedWeapons() {
        return Collections.unmodifiableMap(selectedWeapons);
    }

    public int getTotalSelectedWeapons() {
        int total = 0;
        for (Integer amount : selectedWeapons.values()) {
            total += amount;
        }
        return total;
    }

    public int getWeaponSlotsLimit() {
        return selectedCharacter == null ? 0 : selectedCharacter.getMaxWeaponSlots();
    }

    public List<String> getSelectionLines() {
        List<String> lines = new ArrayList<>();

        if (selectedCharacter != null) {
            lines.add("Character: " + selectedCharacter.getName());
        } else {
            lines.add("Character: (none)");
        }

        lines.add("");
        lines.add("Items:");
        if (selectedItems.isEmpty()) {
            lines.add("- (none)");
        } else {
            for (Map.Entry<Item, Integer> entry : selectedItems.entrySet()) {
                lines.add("- " + entry.getKey().getName() + " x" + entry.getValue());
            }
        }

        lines.add("");
        lines.add("Weapons (" + getTotalSelectedWeapons() + "/" + getWeaponSlotsLimit() + "):");
        if (selectedWeapons.isEmpty()) {
            lines.add("- (none)");
        } else {
            Stats stats = calculateBuildStats();
            for (Map.Entry<Weapon, Integer> entry : selectedWeapons.entrySet()) {
                ComputedWeaponStats computed = calculateWeaponStats(entry.getKey(), stats);
                lines.add("- " + entry.getKey().getName() + " x" + entry.getValue()
                        + " | DMG " + String.format("%.2f", computed.getDamage())
                        + " | ASPD " + String.format("%.2f", computed.getAttackSpeed()));
            }
        }

        return lines;
    }

    public void saveBuild(Path path) throws IOException {
        persistence.save(path, selectedCharacter, selectedItems, selectedWeapons);
    }

    public void loadBuild(Path path) throws IOException {
        BuildPersistence.LoadedBuild loaded = persistence.load(path, catalog);

        resetBuild();
        if (loaded.character != null) {
            selectedCharacter = loaded.character;
            if (loaded.weapons.isEmpty()) {
                applyCharacterStartEffects();
            }
        }

        for (Map.Entry<Item, Integer> entry : loaded.items.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                addItem(entry.getKey());
            }
        }

        for (Map.Entry<Weapon, Integer> entry : loaded.weapons.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                addWeapon(entry.getKey());
            }
        }
    }

    private void applyCharacterStartEffects() {
        if (selectedCharacter == null) {
            return;
        }

        BuildContext ctx = new BuildContext(selectedCharacter, new ArrayList<>(), flattenWeapons(selectedWeapons));
        for (StartEffect effect : selectedCharacter.getStartEffects()) {
            effect.apply(ctx);
        }

        selectedWeapons.clear();
        for (Weapon weapon : ctx.getWeapons()) {
            addWeapon(weapon);
        }
    }

    private double getDamageBonusByType(Weapon weapon, Stats buildStats) {
        double generic = buildStats.getStat(Stat.DAMAGE);
        switch (weapon.getType()) {
            case MELEE:
                return generic + buildStats.getStat(Stat.MELEE_DAMAGE);
            case RANGED:
                return generic + buildStats.getStat(Stat.RANGED_DAMAGE);
            case ELEMENTAL:
                return generic + buildStats.getStat(Stat.ELEMENTAL_DAMAGE);
            default:
                return generic;
        }
    }

    private List<Item> flattenItems(Map<Item, Integer> map) {
        List<Item> out = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                out.add(entry.getKey());
            }
        }
        return out;
    }

    private List<Weapon> flattenWeapons(Map<Weapon, Integer> map) {
        List<Weapon> out = new ArrayList<>();
        for (Map.Entry<Weapon, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                out.add(entry.getKey());
            }
        }
        return out;
    }
}
