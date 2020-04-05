package enigma;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Abel Feleke
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (pawls >= numRotors) {
            throw new EnigmaException("too many pawls");
        }
        _alphabet = alpha;
        _pawls = pawls;
        _numRotors = numRotors;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        insertedRotors = new Rotor[rotors.length];
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor r: _allRotors) {
                if (r.name().equals(rotors[i])) {
                    insertedRotors[i] = r;
                    if (i < numRotors() - numPawls()
                            && insertedRotors[i].rotates()) {
                        throw new EnigmaException("not supposed to rotate");
                    } else if (i >= numRotors() - numPawls()
                            && !insertedRotors[i].rotates()) {
                        throw new EnigmaException("rotors should rotate");
                    }
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != insertedRotors.length - 1) {
            throw new EnigmaException("incorrect number of settings");
        }
        if (!insertedRotors[0].reflecting()) {
            throw new EnigmaException("missing reflector");
        }
        for (int i = 0; i < setting.length(); i++) {
            char c = setting.charAt(i);
            if (!_alphabet.contains(c)) {
                throw new EnigmaException("setting not in alphabet");
            }
            insertedRotors[i + 1].set(c);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        char letter = _alphabet.toChar(c);
        boolean[] notch = new boolean[numPawls()];
        int mRotors = numRotors() - numPawls();
        for (int mIndex = mRotors + 1; mIndex < numRotors(); mIndex++) {
            if (insertedRotors[mIndex].atNotch()) {
                notch[mIndex - mRotors] = true;
                notch[mIndex - mRotors - 1] = true;
            }
        }
        notch[numPawls() - 1] = true;
        for (int i = 0; i < notch.length; i++) {
            if (notch[i]) {
                insertedRotors[i + mRotors].advance();
            }
        }
        c  = plugBoard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i--) {
            c = insertedRotors[i].convertForward(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            c = insertedRotors[i].convertBackward(c);
        }
        c = plugBoard.invert(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll("\\s", "");
        char[] decode = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            decode[i] =
                _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return new String(decode);
    }
    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Rotor pawls. */
    private int _pawls;

    /** Plug Board. */
    private Permutation pBoard;

    /** Collection of all rotors. */
    private Collection<Rotor> _allRotors;

    /** plugboard. */
    private Permutation plugBoard;

    /** inserted rotors. */
    private Rotor[] insertedRotors;

}
