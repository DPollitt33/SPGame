package JavaFiles.StatusEffects;

import JavaFiles.Characters.EndTurnResult;
import JavaFiles.Characters.StatusEffect;

/**
 * Created by AlexC on 4/10/2015.
 */
public class Cleanse_StatusEffect extends StatusEffect {

    // default constructor for an object of type cleanse_statusEffect
    public Cleanse_StatusEffect()
    {
        super("Cleanse");
    }

    @Override
    //performs the end turn checks on this character
    // cleanse will remove all status effects which are applied to the character
    public EndTurnResult endTurnCheck(EndTurnResult result)
    {
        result.clearStatusEffects();
        return result;
    }
}
