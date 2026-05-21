package BrotatoBuildPlanner.Vista.Components;

import BrotatoBuildPlanner.Controlador.BrotatoController;
import BrotatoBuildPlanner.Modelo.BuildManager;
import BrotatoBuildPlanner.Modelo.Catalog.GameCatalog;
import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Item.ItemCategory;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Stats.Stats;
import BrotatoBuildPlanner.Modelo.Item.ItemTier;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Vista principal de construccion de builds en JavaFX.
 */
public class PanelBuild extends BorderPane {
    private final GameCatalog catalog;
    private final BuildManager buildManager;
    private final Stage owner;

    private final ObservableList<Character> characterModel;
    private final ObservableList<Item> itemModel;
    private final ObservableList<Weapon> weaponModel;
    private final ObservableList<String> selectedModel;

    private final ListView<Character> characterList;
    private final ListView<Item> itemList;
    private final ListView<Weapon> weaponList;
    private final ListView<String> selectedList;

    private final Label lblMaxHP;
    private final Label lblRegeneration;
    private final Label lblLifeSteal;
    private final Label lblDamage;
    private final Label lblMelee;
    private final Label lblRanged;
    private final Label lblElemental;
    private final Label lblAttackSpeed;
    private final Label lblCritChance;
    private final Label lblEngineering;
    private final Label lblRange;
    private final Label lblArmor;
    private final Label lblDodge;
    private final Label lblSpeed;
    private final Label lblLuck;
    private final Label lblHarvesting;

    private final DecimalFormat df;

    public PanelBuild(Stage owner) {
        this.owner = owner;
        this.catalog = BrotatoController.loadCatalog();
        this.buildManager = new BuildManager(catalog);

        this.characterModel = FXCollections.observableArrayList();
        this.itemModel = FXCollections.observableArrayList();
        this.weaponModel = FXCollections.observableArrayList();
        this.selectedModel = FXCollections.observableArrayList();

        this.characterList = new ListView<>(characterModel);
        this.itemList = new ListView<>(itemModel);
        this.weaponList = new ListView<>(weaponModel);
        this.selectedList = new ListView<>(selectedModel);

        this.lblMaxHP = new Label();
        this.lblRegeneration = new Label();
        this.lblLifeSteal = new Label();
        this.lblDamage = new Label();
        this.lblMelee = new Label();
        this.lblRanged = new Label();
        this.lblElemental = new Label();
        this.lblAttackSpeed = new Label();
        this.lblCritChance = new Label();
        this.lblEngineering = new Label();
        this.lblRange = new Label();
        this.lblArmor = new Label();
        this.lblDodge = new Label();
        this.lblSpeed = new Label();
        this.lblLuck = new Label();
        this.lblHarvesting = new Label();

        this.df = new DecimalFormat("0.##");

        buildUi();
        bindEvents();
        loadCatalogIntoLists();
        refreshView();
    }

    private void buildUi() {
        setPadding(new Insets(10));

        Label title = new Label("Brotato Build Planner");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        setTop(title);

        VBox left = new VBox(8,
                wrap("Characters", characterList),
                wrap("Items", itemList),
                wrap("Weapons", weaponList));
        VBox.setVgrow(characterList, Priority.ALWAYS);
        VBox.setVgrow(itemList, Priority.ALWAYS);
        VBox.setVgrow(weaponList, Priority.ALWAYS);

        VBox right = new VBox(8,
                wrap("Current Build", selectedList),
                buildStatsPanel());
        VBox.setVgrow(selectedList, Priority.ALWAYS);

        HBox center = new HBox(10, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        setCenter(center);

        Button btnSave = new Button("Save");
        Button btnLoad = new Button("Load");
        Button btnBack = new Button("Back");

        btnSave.setOnAction(e -> saveBuild());
        btnLoad.setOnAction(e -> loadBuild());
        btnBack.setOnAction(e -> {
            buildManager.resetBuild();
            refreshView();
        });

        HBox bottom = new HBox(8, btnSave, btnLoad, btnBack);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(8, 0, 0, 0));
        setBottom(bottom);

        installCellFactories();
    }

    private VBox buildStatsPanel() {
        VBox stats = new VBox(3,
                new Label("Stats"),
                new Separator(),
                lblMaxHP,
                lblRegeneration,
                lblLifeSteal,
                lblDamage,
                lblMelee,
                lblRanged,
                lblElemental,
                lblAttackSpeed,
                lblCritChance,
                lblEngineering,
                lblRange,
                lblArmor,
                lblDodge,
                lblSpeed,
                lblLuck,
                lblHarvesting);
        return wrap("Build Stats", stats);
    }

    private VBox wrap(String title, Node content) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(6, titleLabel, content);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-border-color: #c5c5c5; -fx-border-radius: 6; -fx-background-radius: 6;");
        VBox.setVgrow(content, Priority.ALWAYS);
        return box;
    }

    private void installCellFactories() {
        characterList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Character item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }
                setText(item.getName());
                setTooltip(new Tooltip(item.getName() + "\n" + item.getDescription() + "\nWeapon slots: " + item.getMaxWeaponSlots()));
            }
        });

        itemList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }
                String limit = item.getCategory() == ItemCategory.LIMITED || item.getCategory() == ItemCategory.UNIQUE
                        ? "\nLimit: " + item.getMaxStack()
                        : "";
                setText(item.getName());
                setTooltip(new Tooltip(item.getName() + "\n" + item.getDescription()
                    + "\nTier: " + formatTierLabel(item.getTier())
                    + "\nType: " + item.getCategory() + limit));
            }
        });

        weaponList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Weapon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }
                setText(item.getName());
                setTooltip(new Tooltip(item.getName() + "\n" + item.getDescription()
                    + "\nTier: " + formatTierLabel(ItemTier.fromRarity(item.getWeaponTier()))
                    + "\nType: " + item.getType()
                    + "\nSets: " + item.getSet1() + ", " + item.getSet2()
                    + "\nBase dmg: " + df.format(item.getDamage())));
            }
        });
    }

    private void bindEvents() {
        characterList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Character picked = characterList.getSelectionModel().getSelectedItem();
                if (picked != null) {
                    onCharacterPicked(picked);
                }
            }
        });

        itemList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Item picked = itemList.getSelectionModel().getSelectedItem();
                if (picked != null) {
                    if (buildManager.getSelectedCharacter() == null) {
                        showWarning("Personaje requerido", "Primero debes seleccionar un personaje.");
                        return;
                    }
                    boolean ok = buildManager.addItem(picked);
                    if (!ok) {
                        showInfo("Limite de item", "No se puede anadir mas de este item (limite alcanzado).");
                    }
                    refreshView();
                }
            }
        });

        weaponList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Weapon picked = weaponList.getSelectionModel().getSelectedItem();
                if (picked != null) {
                    if (buildManager.getSelectedCharacter() == null) {
                        showWarning("Personaje requerido", "Primero debes seleccionar un personaje.");
                        return;
                    }
                    boolean ok = buildManager.addWeapon(picked);
                    if (!ok) {
                        showInfo("Limite de arma", "No se puede anadir el arma: has alcanzado el limite del personaje o del arma.");
                    }
                    refreshView();
                }
            }
        });
    }

    // Devuelve una etiqueta de tier amigable
    private String formatTierLabel(ItemTier tier) {
        switch (tier) {
            case COMMON:
                return "Common";
            case UNCOMMON:
                return "Uncommon";
            case RARE:
                return "Rare";
            case EPIC:
                return "Epic";
            case LEGENDARY:
                return "Legendary";
            default:
                return tier.name();
        }
    }

    private void onCharacterPicked(Character picked) {
        if (buildManager.hasBuildProgress() && buildManager.getSelectedCharacter() != null
                && buildManager.getSelectedCharacter() != picked) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Cambiar de personaje reiniciara la build actual. Deseas continuar?",
                    ButtonType.OK,
                    ButtonType.CANCEL);
            alert.setHeaderText("Confirmar cambio de personaje");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }

        buildManager.selectCharacter(picked);
        refreshView();
    }

    private void loadCatalogIntoLists() {
        characterModel.setAll(catalog.getCharacters());
        itemModel.setAll(catalog.getItems());
        refreshWeaponListForSelectedCharacter();
    }

    private void refreshWeaponListForSelectedCharacter() {
        Character selected = buildManager.getSelectedCharacter();
        if (selected == null) {
            weaponModel.setAll(catalog.getWeapons());
            return;
        }

        List<Weapon> filtered = new ArrayList<>();
        for (Weapon weapon : catalog.getWeapons()) {
            if (buildManager.canEquipWeapon(weapon)) {
                filtered.add(weapon);
            }
        }
        weaponModel.setAll(filtered);
    }

    private void refreshView() {
        refreshWeaponListForSelectedCharacter();
        selectedModel.setAll(buildManager.getSelectionLines());

        Stats stats = buildManager.calculateBuildStats();
        lblMaxHP.setText("Max HP: " + df.format(stats.getStat(Stat.MAX_HP)));
        lblRegeneration.setText("HP Regeneration: " + df.format(stats.getStat(Stat.HP_REGEN)));
        lblLifeSteal.setText("Life Steal: " + df.format(stats.getStat(Stat.LIFE_STEAL)));
        lblDamage.setText("Damage: " + df.format(stats.getStat(Stat.DAMAGE)) + "%");
        lblMelee.setText("Melee Damage: " + df.format(stats.getStat(Stat.MELEE_DAMAGE)));
        lblRanged.setText("Ranged Damage: " + df.format(stats.getStat(Stat.RANGED_DAMAGE)));
        lblElemental.setText("Elemental Damage: " + df.format(stats.getStat(Stat.ELEMENTAL_DAMAGE)));
        lblAttackSpeed.setText("Attack Speed: " + df.format(stats.getStat(Stat.ATTACK_SPEED)) + "%");
        lblCritChance.setText("Crit Chance: " + df.format(stats.getStat(Stat.CRIT_CHANCE)) + "%");
        lblEngineering.setText("Engineering: " + df.format(stats.getStat(Stat.ENGINEERING)));
        lblRange.setText("Range: " + df.format(stats.getStat(Stat.RANGE)));
        lblArmor.setText("Armor: " + df.format(stats.getStat(Stat.ARMOR)));
        lblDodge.setText("Dodge: " + df.format(stats.getStat(Stat.DODGE)) + "%");
        lblSpeed.setText("Speed: " + df.format(stats.getStat(Stat.SPEED)) + "%");
        lblLuck.setText("Luck: " + df.format(stats.getStat(Stat.LUCK)));
        lblHarvesting.setText("Harvesting: " + df.format(stats.getStat(Stat.HARVESTING)));
    }

    private void saveBuild() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar build (JSON)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = chooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            buildManager.saveBuild(path);
            showInfo("Build", "Build guardada correctamente.");
        } catch (IOException ex) {
            showError("Error", "No se pudo guardar la build: " + ex.getMessage());
        }
    }

    private void loadBuild() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Cargar build (JSON)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = chooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            buildManager.loadBuild(path);
            refreshView();
            Character selected = buildManager.getSelectedCharacter();
            if (selected != null) {
                characterList.getSelectionModel().select(selected);
            }
            showInfo("Build", "Build cargada correctamente.");
        } catch (IOException ex) {
            showError("Error", "No se pudo cargar la build: " + ex.getMessage());
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }
}
