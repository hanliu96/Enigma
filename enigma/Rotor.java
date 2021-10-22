package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Han Liu 3034272830 cs61b-agl
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ringStellung = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Return my current ringstellung. */
    int settingRing() {
        return _ringStellung;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Set ringstellung to character RPSON. */
    void setRing(char rpson) {
        _ringStellung = alphabet().toInt(rpson);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int pIntIn = _permutation.wrap(p + _setting - _ringStellung);
        int pIntOut = _permutation.permute(pIntIn);
        int result = _permutation.wrap(pIntOut - _setting + _ringStellung);
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int eIntIn = _permutation.wrap(e + _setting - _ringStellung);
        int eIntOut = _permutation.invert(eIntIn);
        int result = _permutation.wrap(eIntOut - _setting + _ringStellung);
        return result;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean justPassNotch() {
        return false;
    }


    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** My current setting. */
    private int _setting;

    /** Ringstellung setting. */
    private  int _ringStellung;

}
