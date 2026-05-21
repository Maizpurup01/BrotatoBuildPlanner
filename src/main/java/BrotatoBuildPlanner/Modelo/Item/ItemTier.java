package BrotatoBuildPlanner.Modelo.Item;

/**
 * Tier de rareza para personajes, objetos y armas.
 */
public enum ItemTier {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    public static ItemTier fromRarity(int rarity) {
        switch (rarity) {
            case 1:
                return COMMON;
            case 2:
                return RARE;
            case 3:
                return EPIC;
            case 4:
                return LEGENDARY;
            default:
                return COMMON;
        }
    }
}
