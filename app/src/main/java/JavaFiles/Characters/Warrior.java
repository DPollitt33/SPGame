package JavaFiles.Characters;

import java.util.ArrayList;
import java.util.List;

import JavaFiles.*;
import JavaFiles.Character;

/**
 * Created by AlexC on 3/26/2015.
 */
public class Warrior extends JavaFiles.Character {


    @Override
    // returns the list of status effects to be applied as is
    protected List<Character> checkAppliedStatusEffects(List<Character> effects) {
        return effects;
    }

    @Override
    // applies a status effect to this character
    public MoveResult hitByEffect(Effect effect) {
        //get the damage the move inflicts
        int damage = effect.getBase_damage();

        //lower this classes health by the amount of damage
        stat.modifyHealth(damage);

        // get the status effects from the move that were applied
        List<Character> effects = checkAppliedStatusEffects(effect.getStatus_effects());
        //status effects add their name to a list and call the next method, continue until we hit the base class
        //then pass a list of currently afflicted statuses back up

        // apply the status effects by wrapping this class with them
        // and get a reference to the final character object with the effects applied
        Character resultingCharacter = applyStatusEffects(effects);

        // create an array list to return our list of damages and status effects applied to this character
        ArrayList<String> result = new ArrayList<String>();

        result.add(damage + "");

        for(Character efct : effects)
        {
              result.add(efct.getName());
        }

        // return the move result with the resulting character and a list of strings which has
        // the damage inflicted, and secondly the names of all effects applied
        return new MoveResult(resultingCharacter, result);
    }

    @Override
    // Applies the list of status effects to this character
    public Character applyStatusEffects(List<Character> effects) {
        // set up a result to store our new character objects
        // initially, set it to this character for if we don't need to make changes
        Character resultingCharacter = this;

        // check if we have any effects left to apply
        if(effects.size() > 0) {
            // if so, apply one and call the applyStatusEffects method on the resulting Character object
            resultingCharacter = ((StatusEffect) effects.get(0)).setCharacter(this);
        }
        else if(effects.size() > 1){
            // if we have at least one more status effect to apply, apply it on the resulting object which we
            // have already modified from before
            return resultingCharacter.applyStatusEffects(effects.subList(1,effects.size()));
        }
        else {
            // otherwise we are done so return our resulting character
            // which will be wrapped once if we had 1 change, or this if we had 0 changes to make
            return resultingCharacter;
        }
        // make the compiler happy =)
        return resultingCharacter;
    }
}