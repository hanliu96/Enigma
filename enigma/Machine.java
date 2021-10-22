package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Han Liu 3034272830 cs61b-agl
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
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
        _rotorArray = new Rotor[_numRotors];
        HashMap<String, Rotor> rotorHashMap = new HashMap<>();
        for (Rotor aRotor : _allRotors) {
            rotorHashMap.put(aRotor.name(), aRotor);
        }

        for (int i = 0; i < rotors.length; i++) {
            String rotorName = rotors[i];
            if (rotorHashMap.containsKey(rotorName)) {
                _rotorArray[i] = rotorHashMap.get(rotorName);
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _numRotors; i++) {
            _rotorArray[i].set(setting.charAt(i - 1));
        }
    }

    /** Extra credit part using SETTING and RINGSTELLUNG. */
    void setRotors(String setting, String ringStellung) {
        for (int i = 1; i < _numRotors; i++) {
            _rotorArray[i].set(setting.charAt(i - 1));
            _rotorArray[i].setRing(ringStellung.charAt(i - 1));
        }
    }
    /** - ringStellung.charAt(i - 1) */

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        boolean[] adv = new boolean[_numRotors - 1];
        _rotorArray[_numRotors - 1].advance();
        adv[_numRotors - 2] = true;
        for (int i = _numRotors - 1; i > 0; i--) {
            if (i > 2) {
                if ((_rotorArray[i].justPassNotch()
                        || _rotorArray[i - 1].atNotch())
                        && _rotorArray[i - 1].rotates() && adv[i - 1]) {
                    _rotorArray[i - 1].advance();
                    adv[i - 2] = true;
                }
            } else {
                if ((_rotorArray[i].justPassNotch())
                        && _rotorArray[i - 1].rotates() && adv[i - 1]) {
                    _rotorArray[i - 1].advance();
                    adv[i - 2] = true;
                }
            }

        }

        int result = _plugBoard.permute(c);
        for (int i = _numRotors - 1; i >= 0; i--) {
            result = _rotorArray[i].convertForward(result);
        }

        for (int j = 1; j < _numRotors; j++) {
            result = _rotorArray[j].convertBackward(result);
        }

        result = _plugBoard.permute(result);

        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll(" ", "");
        String[] magArray = msg.split("");
        int[] magIntArray = new int[magArray.length];
        for (int i = 0; i < magArray.length; i++) {
            magIntArray[i] = _alphabet.toInt(magArray[i].charAt(0));
        }

        char[] msgCharArray = new char[magArray.length];
        String[] magOut = new String[magArray.length];
        for (int j = 0; j < magArray.length; j++) {
            msgCharArray[j] = _alphabet.toChar(convert(magIntArray[j]));
            magOut[j] = Character.toString(msgCharArray[j]);
        }

        String result = "";
        for (int k = 0; k < magArray.length; k++) {
            result += magOut[k];
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** ADDITIONAL FIELDS NEEDED. */
    private int _numRotors;

    /** ADDITIONAL FIELDS NEEDED. */
    private int _pawls;

    /** ADDITIONAL FIELDS NEEDED. */
    private Collection<Rotor> _allRotors;

    /** ADDITIONAL FIELDS NEEDED. */
    private Rotor[] _rotorArray;

    /** ADDITIONAL FIELDS NEEDED. */
    private Permutation _plugBoard;


}
