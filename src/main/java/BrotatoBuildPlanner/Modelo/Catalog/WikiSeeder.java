package BrotatoBuildPlanner.Modelo.Catalog;

import BrotatoBuildPlanner.Controlador.Database.CreateTables;
import BrotatoBuildPlanner.Controlador.Database.Database;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Seeder de base de datos usando las plantillas del wiki de Brotato.
 */
public final class WikiSeeder {

    private static final String BASE = "https://brotato.wiki.spellsandguns.com/index.php?title=%s&action=raw";

    private static final String ITEM_TEMPLATE = "Template:Item_Data";
    private static final String WEAPON_TEMPLATE = "Template:Weapon_Data";
    private static final String CHARACTER_TEMPLATE = "Template:Character_Data";
    private static final String WEAPON_CLASS_TEMPLATE = "Template:WeaponClass";

    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^{}]+)\\}\\}");
    private static final Pattern LINK_WITH_TEXT_PATTERN = Pattern.compile("\\[\\[[^\\]|]+\\|([^\\]]+)\\]\\]");
    private static final Pattern LINK_SIMPLE_PATTERN = Pattern.compile("\\[\\[([^\\]|]+)\\]\\]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[+-]?\\d+(?:\\.\\d+)?");
    private static final Pattern THRESHOLD_BONUS_PATTERN = Pattern.compile("\\((\\d)\\)\\s*([^()]+?)(?=\\(\\d\\)|$)");
    private static final Pattern START_WEAPON_PATTERN =
            Pattern.compile("(?i)you start with\\s+(\\d+)\\s+([^;,.]+)");

    private static final String TYPE_FLAT = "FLAT";
    private static final String TYPE_PERCENTAGE = "PERCENTAGE";
    private static final String TYPE_MULTIPLIER = "MULTIPLIER";

    private static final String PRIORITY_BASE = "BASE";
    private static final String PRIORITY_FLAT = "FLAT";
    private static final String PRIORITY_PERCENTAGE = "PERCENTAGE";

    private static final Map<String, String> STAT_ALIASES = buildStatAliases();

    private WikiSeeder() {
    }

    public static void main(String[] args) {
        try {
            seedFromWiki();
            System.out.println("Seeder completado correctamente.");
        } catch (Exception ex) {
            System.err.println("Error durante el seeding: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void seedFromWiki() throws IOException, InterruptedException, SQLException {
        CreateTables.createTables();

        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        String itemRaw = fetchRaw(http, ITEM_TEMPLATE);
        String weaponRaw = fetchRaw(http, WEAPON_TEMPLATE);
        String characterRaw = fetchRaw(http, CHARACTER_TEMPLATE);
        String weaponClassRaw = fetchRaw(http, WEAPON_CLASS_TEMPLATE);

        Map<String, String> itemCases = parseTopLevelCases(itemRaw);
        Map<String, String> weaponCases = parseTopLevelCases(weaponRaw);
        Map<String, String> characterCases = parseTopLevelCases(characterRaw);
        Map<String, String> weaponClassCases = parseTopLevelCases(weaponClassRaw);

        System.out.printf("Casos parseados: character=%d, item=%d, weapon=%d, weaponClass=%d%n",
            characterCases.size(), itemCases.size(), weaponCases.size(), weaponClassCases.size());

        try (Connection conn = Database.connect()) {
            if (conn == null) {
                throw new SQLException("No se pudo abrir la conexion con SQLite.");
            }
            conn.setAutoCommit(false);

            try {
                rebuildSchema(conn);
                clearData(conn);

                Map<Integer, String> weaponModifierTexts = new HashMap<>();
                Map<Integer, String> itemModifierTexts = new HashMap<>();
                Map<Integer, String> characterModifierTexts = new HashMap<>();

                Map<String, Integer> weaponIds = insertWeapons(conn, weaponCases, weaponModifierTexts);
                int itemCount = insertItems(conn, itemCases, itemModifierTexts);
                int characterCount = insertCharactersAndStartWeapons(conn, characterCases, weaponIds, characterModifierTexts);

                int weaponModifierLinks = insertEntityModifiers(conn, weaponModifierTexts, "weapon_modifier", "weapon_id");
                int itemModifierLinks = insertEntityModifiers(conn, itemModifierTexts, "item_modifier", "item_id");
                int characterModifierLinks = insertEntityModifiers(conn, characterModifierTexts, "character_modifier", "character_id");
                int setBonusCount = insertWeaponSetBonuses(conn, weaponClassCases);

                conn.commit();
                System.out.printf(Locale.ROOT,
                        "Insertados: %d personajes, %d items, %d armas.%n",
                        characterCount, itemCount, weaponIds.size());
                System.out.printf(Locale.ROOT,
                    "Enlaces de modificador: weapon=%d, item=%d, character=%d; set bonuses=%d.%n",
                    weaponModifierLinks, itemModifierLinks, characterModifierLinks, setBonusCount);
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static String fetchRaw(HttpClient client, String title) throws IOException, InterruptedException {
        String url = String.format(Locale.ROOT, BASE, URLEncoder.encode(title, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "BrotatoBuildPlannerSeeder/1.0")
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("No se pudo descargar " + title + ". HTTP: " + response.statusCode());
        }
        return response.body();
    }

    private static Map<String, String> parseTopLevelCases(String raw) {
        int includeStart = raw.indexOf("<includeonly>");
        int includeEnd = raw.indexOf("</includeonly>");
        String section = raw;
        if (includeStart >= 0 && includeEnd > includeStart) {
            section = raw.substring(includeStart + "<includeonly>".length(), includeEnd);
        }

        section = section.replace("\r", "").replaceAll("(?m)^\\s+\\|", "|");

        int switchStart = section.indexOf("{{#switch:{{lc:{{{1|}}}}}");
        if (switchStart < 0) {
            switchStart = section.indexOf("{{#switch:{{lc:{{{class|{{{1|}}}}}}}}");
        }
        if (switchStart < 0) {
            return Map.of();
        }

        int firstCase = section.indexOf("\n|", switchStart);
        int i = firstCase >= 0 ? firstCase + 1 : section.indexOf('|', switchStart);
        if (i < 0) {
            return Map.of();
        }
        int depth = 1;

        Map<String, String> cases = new LinkedHashMap<>();
        String currentKey = null;
        StringBuilder currentValue = null;

        while (i < section.length()) {
            if (startsWith(section, i, "{{")) {
                depth++;
                if (currentValue != null) {
                    currentValue.append("{{");
                }
                i += 2;
                continue;
            }
            if (startsWith(section, i, "}}")) {
                depth--;
                if (depth <= 0) {
                    if (currentKey != null && currentValue != null && !"#default".equalsIgnoreCase(currentKey)) {
                        cases.putIfAbsent(normalizeNameKey(currentKey), currentValue.toString().trim());
                    }
                    break;
                }
                if (currentValue != null) {
                    currentValue.append("}}");
                }
                i += 2;
                continue;
            }

            if (depth == 1 && isLineStart(section, i) && section.charAt(i) == '|') {
                int lineEnd = section.indexOf('\n', i);
                if (lineEnd < 0) {
                    lineEnd = section.length();
                }
                int eq = section.indexOf('=', i + 1);

                if (eq > 0 && eq < lineEnd) {
                    if (currentKey != null && currentValue != null && !"#default".equalsIgnoreCase(currentKey)) {
                        cases.putIfAbsent(normalizeNameKey(currentKey), currentValue.toString().trim());
                    }
                    currentKey = section.substring(i + 1, eq).trim();
                    currentValue = new StringBuilder();
                    i = eq + 1;
                    continue;
                }
            }

            if (currentValue != null) {
                currentValue.append(section.charAt(i));
            }
            i++;
        }

        return cases;
    }

    private static Map<String, String> parseSwitchFields(String block) {
        int start = block.indexOf("{{#switch:");
        if (start < 0) {
            return Map.of();
        }

        int i = start + "{{#switch:".length();
        int depth = 1;

        Map<String, String> fields = new LinkedHashMap<>();
        String currentKey = null;
        StringBuilder currentValue = null;

        while (i < block.length()) {
            if (startsWith(block, i, "{{")) {
                depth++;
                if (currentValue != null) {
                    currentValue.append("{{");
                }
                i += 2;
                continue;
            }
            if (startsWith(block, i, "}}")) {
                depth--;
                if (depth <= 0) {
                    if (currentKey != null && currentValue != null && !"#default".equalsIgnoreCase(currentKey)) {
                        fields.putIfAbsent(currentKey.trim().toLowerCase(Locale.ROOT), currentValue.toString().trim());
                    }
                    break;
                }
                if (currentValue != null) {
                    currentValue.append("}}");
                }
                i += 2;
                continue;
            }

            if (depth == 1 && isLineStart(block, i) && block.charAt(i) == '|') {
                int lineEnd = block.indexOf('\n', i);
                if (lineEnd < 0) {
                    lineEnd = block.length();
                }
                int eq = block.indexOf('=', i + 1);
                if (eq > 0 && eq < lineEnd) {
                    if (currentKey != null && currentValue != null && !"#default".equalsIgnoreCase(currentKey)) {
                        fields.putIfAbsent(currentKey.trim().toLowerCase(Locale.ROOT), currentValue.toString().trim());
                    }
                    currentKey = block.substring(i + 1, eq).trim();
                    currentValue = new StringBuilder();
                    i = eq + 1;
                    continue;
                }
            }

            if (currentValue != null) {
                currentValue.append(block.charAt(i));
            }
            i++;
        }

        return fields;
    }

    private static int insertItems(
            Connection conn,
            Map<String, String> itemCases,
            Map<Integer, String> itemModifierTexts) throws SQLException {
        int count = 0;
        String sql = "INSERT INTO item(name, description, cuantity, tier) VALUES(?,?,?,?)";

        try (PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Map.Entry<String, String> entry : itemCases.entrySet()) {
                String name = displayName(entry.getKey());
                if (name.isBlank() || "#default".equalsIgnoreCase(name)) {
                    continue;
                }

                Map<String, String> fields = parseSwitchFields(entry.getValue());
                String stats = cleanWikiText(fields.getOrDefault("stats", ""));
                int limit = parseNullableInt(cleanWikiText(fields.getOrDefault("limit", "")), 999);
                int tier = parseNullableInt(cleanWikiText(fields.getOrDefault("rarity", "1")), 1);
                if (limit < 0) {
                    limit = 999;
                }

                st.setString(1, name);
                st.setString(2, stats);
                st.setInt(3, limit == 0 ? 1 : limit);
                st.setInt(4, tier);
                st.executeUpdate();

                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        String modifierText = safeField(fields, "stats", "effects", "effect", "special");
                        if (modifierText.isBlank()) {
                            modifierText = entry.getValue();
                        }
                        itemModifierTexts.put(rs.getInt(1), modifierText);
                    }
                }
                count++;
            }
        }

        return count;
    }

    private static Map<String, Integer> insertWeapons(
            Connection conn,
            Map<String, String> weaponCases,
            Map<Integer, String> weaponModifierTexts) throws SQLException {
        Map<String, Integer> weaponIds = new LinkedHashMap<>();

        String sql = "INSERT INTO weapon(name, description, cuantity, set1, set2, weapon_type, tier, damage, attack_speed, range, lifesteal) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Map.Entry<String, String> entry : weaponCases.entrySet()) {
                String name = displayName(entry.getKey());
                if (name.isBlank() || "#default".equalsIgnoreCase(name)) {
                    continue;
                }

                Map<String, String> fields = parseSwitchFields(entry.getValue());

                String typesRaw = fields.getOrDefault("types", "");
                List<String> typeList = extractTypeList(typesRaw);
                String set1 = mapSet(typeList, 0);
                String set2 = mapSet(typeList, 1);
                String weaponType = inferWeaponType(typeList);

                int tier = parseNullableInt(cleanWikiText(fields.getOrDefault("rarity", "1")), 1);

                double damage = parseTierNumber(fields.get("damage"), tier, 0.0);
                double attackSpeed = parseTierNumber(fields.get("attackspeed"), tier, 1.0);
                int range = (int) Math.round(parseTierNumber(fields.get("range"), tier, 0));
                int lifeSteal = (int) Math.round(parseTierNumber(fields.get("lifesteal"), tier, 0));

                String special = cleanWikiText(fields.getOrDefault("special", ""));
                st.setString(1, name);
                st.setString(2, special);
                st.setInt(3, 6);
                st.setString(4, set1);
                st.setString(5, set2);
                st.setString(6, weaponType);
                st.setInt(7, tier);
                st.setDouble(8, damage);
                st.setDouble(9, attackSpeed);
                st.setInt(10, range);
                st.setInt(11, lifeSteal);
                st.executeUpdate();

                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        weaponIds.put(name.toLowerCase(Locale.ROOT), id);
                        String modifierText = safeField(fields, "stats", "special", "effect");
                        if (modifierText.isBlank()) {
                            modifierText = entry.getValue();
                        }
                        weaponModifierTexts.put(id, modifierText);
                    }
                }
            }
        }

        return weaponIds;
    }

    private static int insertCharactersAndStartWeapons(
            Connection conn,
            Map<String, String> characterCases,
            Map<String, Integer> weaponIds,
            Map<Integer, String> characterModifierTexts) throws SQLException {

        int count = 0;
        String charSql = "INSERT INTO character(name, description, cuantity) VALUES(?,?,?)";
        String startWeaponSql = "INSERT OR IGNORE INTO character_start_weapon(character_id, weapon_id, amount) VALUES(?,?,?)";

        try (PreparedStatement charSt = conn.prepareStatement(charSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement startWeaponSt = conn.prepareStatement(startWeaponSql)) {

            for (Map.Entry<String, String> entry : characterCases.entrySet()) {
                String name = displayName(entry.getKey());
                if (name.isBlank() || "#default".equalsIgnoreCase(name)) {
                    continue;
                }

                Map<String, String> fields = parseSwitchFields(entry.getValue());
                String description = cleanWikiText(fields.getOrDefault("stats", ""));

                charSt.setString(1, name);
                charSt.setString(2, description);
                charSt.setInt(3, 1);
                charSt.executeUpdate();

                int characterId;
                try (ResultSet rs = charSt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        continue;
                    }
                    characterId = rs.getInt(1);
                }

                String modifierText = safeField(fields, "stats", "effect", "special");
                if (modifierText.isBlank()) {
                    modifierText = entry.getValue();
                }
                characterModifierTexts.put(characterId, modifierText);

                Map<String, Integer> startWeapons = extractStartWeapons(description);
                for (Map.Entry<String, Integer> startWeapon : startWeapons.entrySet()) {
                    Integer weaponId = weaponIds.get(startWeapon.getKey().toLowerCase(Locale.ROOT));
                    if (weaponId == null) {
                        continue;
                    }
                    startWeaponSt.setInt(1, characterId);
                    startWeaponSt.setInt(2, weaponId);
                    startWeaponSt.setInt(3, startWeapon.getValue());
                    startWeaponSt.addBatch();
                }

                count++;
            }

            startWeaponSt.executeBatch();
        }

        return count;
    }

    private static int insertEntityModifiers(
            Connection conn,
            Map<Integer, String> entityTextMap,
            String linkTable,
            String entityColumn) throws SQLException {

        int linkCount = 0;
        String linkSql = "INSERT INTO " + linkTable + "(" + entityColumn + ", modifier_id) VALUES(?,?)";

        try (PreparedStatement linkSt = conn.prepareStatement(linkSql)) {
            for (Map.Entry<Integer, String> entry : entityTextMap.entrySet()) {
                List<ParsedModifier> mods = parseModifiersFromText(entry.getValue());
                for (ParsedModifier mod : mods) {
                    int modifierId = insertModifier(conn, mod);
                    linkSt.setInt(1, entry.getKey());
                    linkSt.setInt(2, modifierId);
                    linkSt.addBatch();
                    linkCount++;
                }
            }
            linkSt.executeBatch();
        }

        return linkCount;
    }

    private static int insertWeaponSetBonuses(Connection conn, Map<String, String> weaponClassCases) throws SQLException {
        int count = 0;
        String setSql = "INSERT INTO weapon_set_bonus(set_type, required_amount, modifier_id) VALUES(?,?,?)";

        try (PreparedStatement setSt = conn.prepareStatement(setSql)) {
            for (Map.Entry<String, String> entry : weaponClassCases.entrySet()) {
                String setType = mapSet(List.of(entry.getKey()), 0);
                if ("NOTYPE".equals(setType)) {
                    continue;
                }

                String bonusText = cleanWikiText(entry.getValue());
                Matcher matcher = THRESHOLD_BONUS_PATTERN.matcher(bonusText);
                while (matcher.find()) {
                    int required = parseNullableInt(matcher.group(1), 0);
                    String effectText = matcher.group(2).trim();
                    if (required <= 0 || effectText.isBlank()) {
                        continue;
                    }

                    List<ParsedModifier> mods = parseModifiersFromText(effectText);
                    for (ParsedModifier mod : mods) {
                        int modifierId = insertModifier(conn, mod);
                        setSt.setString(1, setType);
                        setSt.setInt(2, required);
                        setSt.setInt(3, modifierId);
                        setSt.addBatch();
                        count++;
                    }
                }
            }
            setSt.executeBatch();
        }

        return count;
    }

    private static int insertModifier(Connection conn, ParsedModifier mod) throws SQLException {
        String sql = "INSERT INTO modifier(stat, value, type, priority, condition_type, condition_stat, condition_weapon_type, condition_quantity) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, mod.stat);
            st.setDouble(2, mod.value);
            st.setString(3, mod.type);
            st.setString(4, mod.priority);
            st.setString(5, null);
            st.setString(6, null);
            st.setString(7, null);
            st.setObject(8, null);
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener id de modifier insertado.");
    }

    private static List<ParsedModifier> parseModifiersFromText(String text) {
        List<ParsedModifier> out = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return out;
        }

        String cleaned = cleanWikiText(text);

        for (String segment : splitModifierSegments(cleaned)) {
            ParsedModifier parsed = parseSingleModifier(segment);
            if (parsed != null) {
                out.add(parsed);
            }
        }

        return out;
    }

    private static ParsedModifier parseSingleModifier(String rawSegment) {
        String segment = rawSegment.trim();
        if (segment.isBlank()) {
            return null;
        }

        String lower = segment.toLowerCase(Locale.ROOT);
        String stat = findStat(lower);
        if (stat == null) {
            return null;
        }

        Double numeric = parseSignedNumber(segment);
        if (numeric == null) {
            return null;
        }

        if (!startsWithSign(segment) && isNegativeContext(lower)) {
            numeric = -Math.abs(numeric);
        }

        if (lower.contains("modifications are increased by") || lower.contains("modifications are reduced by")) {
            return new ParsedModifier(stat, numeric / 100.0, TYPE_MULTIPLIER, PRIORITY_BASE);
        }
        // En Brotato, "+X% Damage" significa sumar X al contador de Daño%,
        // no escalar el valor actual. Todas las bonificaciones de % son planas (FLAT).
        return new ParsedModifier(stat, numeric, TYPE_FLAT, PRIORITY_FLAT);
    }

    private static List<String> splitModifierSegments(String text) {
        String normalized = text.replace(";", ",");
        String[] parts = normalized.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String token = p.trim();
            if (!token.isBlank()) {
                out.add(token);
            }
        }
        return out;
    }

    private static String findStat(String lowerText) {
        return STAT_ALIASES.entrySet().stream()
                .filter(e -> lowerText.contains(e.getKey()))
                .sorted(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getKey().length()).reversed())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private static String safeField(Map<String, String> fields, String... keys) {
        StringBuilder out = new StringBuilder();
        for (String key : keys) {
            String value = fields.getOrDefault(key, "").trim();
            if (!value.isBlank()) {
                if (out.length() > 0) {
                    out.append(", ");
                }
                out.append(value);
            }
        }
        return out.toString();
    }

    private static Double parseSignedNumber(String text) {
        Matcher m = NUMBER_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static boolean startsWithSign(String text) {
        String t = text.trim();
        return t.startsWith("+") || t.startsWith("-");
    }

    private static boolean isNegativeContext(String lowerText) {
        return lowerText.contains("reduced")
                || lowerText.contains("decreased")
                || lowerText.contains("lose")
                || lowerText.contains("penalty");
    }

    private static void clearData(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("DELETE FROM character_start_weapon");
            st.execute("DELETE FROM weapon_modifier");
            st.execute("DELETE FROM item_modifier");
            st.execute("DELETE FROM character_modifier");
            st.execute("DELETE FROM weapon_set_bonus");
            st.execute("DELETE FROM modifier");
            st.execute("DELETE FROM weapon");
            st.execute("DELETE FROM item");
            st.execute("DELETE FROM character");
        }
    }

    private static void rebuildSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = OFF");
            st.execute("DROP TABLE IF EXISTS character_start_weapon");
            st.execute("DROP TABLE IF EXISTS weapon_modifier");
            st.execute("DROP TABLE IF EXISTS item_modifier");
            st.execute("DROP TABLE IF EXISTS character_modifier");
            st.execute("DROP TABLE IF EXISTS weapon_set_bonus");
            st.execute("DROP TABLE IF EXISTS modifier");
            st.execute("DROP TABLE IF EXISTS weapon");
            st.execute("DROP TABLE IF EXISTS item");
            st.execute("DROP TABLE IF EXISTS character");

            st.execute("CREATE TABLE IF NOT EXISTS character (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS item (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER, tier INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS weapon (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER, set1 TEXT, set2 TEXT, weapon_type TEXT, tier INTEGER, damage REAL, attack_speed REAL, range INTEGER, lifesteal INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS modifier (id INTEGER PRIMARY KEY, stat TEXT, value REAL, type TEXT, priority TEXT, condition_type TEXT, condition_stat TEXT, condition_weapon_type TEXT, condition_quantity INTEGER)");
            st.execute("CREATE TABLE IF NOT EXISTS weapon_set_bonus (id INTEGER PRIMARY KEY, set_type TEXT, required_amount INTEGER, modifier_id INTEGER, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS character_modifier (character_id INTEGER, modifier_id INTEGER, PRIMARY KEY (character_id, modifier_id), FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS item_modifier (item_id INTEGER, modifier_id INTEGER, PRIMARY KEY (item_id, modifier_id), FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS weapon_modifier (weapon_id INTEGER, modifier_id INTEGER, PRIMARY KEY (weapon_id, modifier_id), FOREIGN KEY (weapon_id) REFERENCES weapon(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE IF NOT EXISTS character_start_weapon (character_id INTEGER, weapon_id INTEGER, amount INTEGER, PRIMARY KEY (character_id, weapon_id), FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE, FOREIGN KEY (weapon_id) REFERENCES weapon(id) ON DELETE CASCADE)");
            st.execute("PRAGMA foreign_keys = ON");
        }
    }

    private static List<String> extractTypeList(String rawTypes) {
        String clean = cleanWikiText(rawTypes);
        if (clean.isBlank()) {
            return List.of();
        }

        Set<String> ordered = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String t : splitCommaList(clean)) {
            if (!t.isBlank() && !"-".equals(t)) {
                ordered.add(t.trim());
            }
        }
        return new ArrayList<>(ordered);
    }

    private static String inferWeaponType(List<String> typeList) {
        String joined = String.join(",", typeList).toLowerCase(Locale.ROOT);
        if (joined.contains("gun")) {
            return "RANGED";
        }
        if (joined.contains("elemental")) {
            return "ELEMENTAL";
        }
        if (joined.contains("support") || joined.contains("tool") || joined.contains("medical")) {
            return "SUPPORT";
        }
        return "MELEE";
    }

    private static String mapSet(List<String> typeList, int index) {
        if (index < 0 || index >= typeList.size()) {
            return "NOTYPE";
        }
        String token = typeList.get(index)
                .replace("'", "")
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');

        List<String> allowed = Arrays.asList(
                "NAVAL", "HEAVY", "BLUNT", "PRIMITIVE", "BLADE", "TOOL", "MEDICAL",
                "UNARMED", "PRECISE", "LEGENDARY", "EXPLOSIVE", "ELEMENTAL", "ETHEREAL",
                "SUPPORT", "MEDIEVAL", "MUSICAL", "GUN");

        return allowed.contains(token) ? token : "NOTYPE";
    }

    private static double parseTierNumber(String tierBlock, int preferredTier, double fallback) {
        if (tierBlock == null || tierBlock.isBlank()) {
            return fallback;
        }

        Map<String, String> tierFields = parseSwitchFields(tierBlock);
        List<String> keys = List.of(
                preferredTier + "|common",
                preferredTier + "|rare",
                preferredTier + "|epic",
                preferredTier + "|legendary",
                "1|common",
                "2|rare",
                "3|epic",
                "4|legendary"
        );

        for (String key : keys) {
            String raw = tierFields.get(key.toLowerCase(Locale.ROOT));
            if (raw == null) {
                continue;
            }
            String clean = cleanWikiText(raw);
            if (clean.isBlank() || "-".equals(clean) || "--".equals(clean)) {
                continue;
            }
            return parseFirstNumber(clean, fallback);
        }

        return parseFirstNumber(cleanWikiText(tierBlock), fallback);
    }

    private static int parseNullableInt(String text, int fallback) {
        Matcher m = NUMBER_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group().split("\\.")[0]);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static double parseFirstNumber(String text, double fallback) {
        Matcher m = NUMBER_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group());
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static List<String> splitCommaList(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String[] parts = text.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String value = p.trim();
            if (!value.isBlank() && !"-".equals(value)) {
                list.add(value);
            }
        }
        return list;
    }

    private static Map<String, Integer> extractStartWeapons(String description) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (description == null || description.isBlank()) {
            return out;
        }

        Matcher matcher = START_WEAPON_PATTERN.matcher(description);
        while (matcher.find()) {
            int amount = parseNullableInt(matcher.group(1), 0);
            if (amount <= 0) {
                continue;
            }

            String name = matcher.group(2).trim().replaceFirst("(?i)^cursed\\s+", "");
            if (name.isBlank()) {
                continue;
            }

            out.merge(name, amount, Integer::sum);
        }

        return out;
    }

    private static String cleanWikiText(String input) {
        if (input == null) {
            return "";
        }

        String text = input;
        text = text.replace("\r", "");
        text = text.replace("<br>", "; ").replace("<br/>", "; ").replace("<br />", "; ");
        text = text.replace("&nbsp;", " ");

        text = TAG_PATTERN.matcher(text).replaceAll("");

        String previous;
        do {
            previous = text;
            text = TEMPLATE_PATTERN.matcher(text).replaceAll(match -> {
                String body = match.group(1);
                String[] parts = body.split("\\|");
                if (parts.length == 0) {
                    return "";
                }
                if ("staticon".equalsIgnoreCase(parts[0].trim()) && parts.length > 1) {
                    return " " + parts[1].replace("_", " ").trim();
                }
                if (parts.length >= 3) {
                    return parts[parts.length - 1].trim();
                }
                if (parts.length == 2) {
                    return parts[1].trim();
                }
                return parts[0].trim();
            });
        } while (!text.equals(previous));

        text = LINK_WITH_TEXT_PATTERN.matcher(text).replaceAll("$1");
        text = LINK_SIMPLE_PATTERN.matcher(text).replaceAll("$1");

        text = text.replace("''", "");
        text = text.replace("{{", "").replace("}}", "");
        text = text.replace("|", " ");
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }

    private static String normalizeNameKey(String key) {
        if (key == null) {
            return "";
        }
        String trimmed = key.trim();
        if (trimmed.contains("\n|")) {
            String[] parts = trimmed.split("\\n\\|");
            if (parts.length > 0) {
                trimmed = parts[0].trim();
            }
        }
        if (trimmed.contains("|")) {
            trimmed = trimmed.split("\\|")[0].trim();
        }
        return trimmed;
    }

    private static String displayName(String normalizedKey) {
        if (normalizedKey == null || normalizedKey.isBlank()) {
            return "";
        }
        String[] words = normalizedKey.trim().split("\\s+");
        StringBuilder out = new StringBuilder();
        for (String w : words) {
            if (w.isBlank()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }
            if (w.length() == 1) {
                out.append(w.toUpperCase(Locale.ROOT));
            } else {
                out.append(Character.toUpperCase(w.charAt(0)))
                        .append(w.substring(1));
            }
        }
        return out.toString();
    }

    private static Map<String, String> buildStatAliases() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("hp regeneration", "HP_REGEN");
        m.put("life steal", "LIFE_STEAL");
        m.put("crit chance", "CRIT_CHANCE");
        m.put("dodge chance", "DODGE");
        m.put("elemental damage", "ELEMENTAL_DAMAGE");
        m.put("ranged damage", "RANGED_DAMAGE");
        m.put("melee damage", "MELEE_DAMAGE");
        m.put("attack speed", "ATTACK_SPEED");
        m.put("max hp", "MAX_HP");
        m.put("harvesting", "HARVESTING");
        m.put("engineering", "ENGINEERING");
        m.put("armor", "ARMOR");
        m.put("dodge", "DODGE");
        m.put("luck", "LUCK");
        m.put("range", "RANGE");
        m.put("speed", "SPEED");
        m.put("curse", "CURSE");
        m.put("damage", "DAMAGE");
        return m;
    }

    private static final class ParsedModifier {
        private final String stat;
        private final double value;
        private final String type;
        private final String priority;

        private ParsedModifier(String stat, double value, String type, String priority) {
            this.stat = stat;
            this.value = value;
            this.type = type;
            this.priority = priority;
        }
    }

    private static boolean startsWith(String text, int index, String token) {
        return index >= 0 && index + token.length() <= text.length() && text.startsWith(token, index);
    }

    private static boolean isLineStart(String text, int index) {
        return index == 0 || text.charAt(index - 1) == '\n';
    }
}
