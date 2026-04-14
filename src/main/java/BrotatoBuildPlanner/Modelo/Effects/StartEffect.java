package BrotatoBuildPlanner.Modelo.Effects;

import BrotatoBuildPlanner.Modelo.BuildContext;

/**
 * inerfaz que efectos de personajes(de momento solo hay de armas)
 *
 * @author Manuel
 */
public interface StartEffect {
    void apply(BuildContext context);
}
