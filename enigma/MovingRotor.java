package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Han Liu 3034272830 cs61b-agl
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notch = notches;
        _permutation = perm;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        String[] splitNotches = _notch.split("");
        for (int i = 0; i < splitNotches.length; i++) {
            if (super.setting()
                    == _permutation.wrap(alphabet()
                    .toInt(splitNotches[i].charAt(0)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean justPassNotch() {
        String[] splitNotches = _notch.split("");
        for (int i = 0; i < splitNotches.length; i++) {
            if (super.setting() == _permutation.wrap(alphabet()
                    .toInt(splitNotches[i].charAt(0)) + 1)) {
                return true;
            }
        }
        return false;
    }



    @Override
    void advance() {
        super.set(_permutation.wrap(super.setting() + 1));
    }

    /** Additional field. */
    private String _notch;

    /** Additional field. */
    private Permutation _permutation;

}
