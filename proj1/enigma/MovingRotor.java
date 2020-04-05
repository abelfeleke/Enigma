package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Abel Feleke
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }


    @Override
    boolean atNotch() {
        for (char n: _notches.toCharArray()) {
            if (n == alphabet().toChar(setting())) {
                return true;
            }
        }
        return false;
    }
    @Override
    boolean rotates() {
        return true;
    }
    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    /** String of notches. */
    private String _notches;

}
