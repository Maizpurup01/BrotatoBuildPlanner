package BrotatoBuildPlanner.Modelo.Weapon;

/**
 * Resultado final de estadisticas de un arma tras aplicar la build.
 */
public class ComputedWeaponStats {
    private final double damage;
    private final double attackSpeed;
    private final double range;
    private final double lifeSteal;

    public ComputedWeaponStats(double damage, double attackSpeed, double range, double lifeSteal) {
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
        this.lifeSteal = lifeSteal;
    }

    public double getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getRange() {
        return range;
    }

    public double getLifeSteal() {
        return lifeSteal;
    }
}
