package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Han Liu 3034272830 cs61b-agl
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = _cycles.concat(cycle);
    }

    /** Return the alphabet groups in CYCLE after splitting. */
    static String[] splitCycle(String cycle) {
        if (cycle.contains(" ")) {
            cycle = cycle.replaceAll(" ", "");
            String[] splited = cycle.split("\\)\\(");

            splited[0] = splited[0].substring(1);
            int lastInd = splited.length - 1;
            splited[lastInd] = splited[lastInd].substring(0,
                    splited[lastInd].length() - 1);
            return splited;
        } else {
            cycle = cycle.replaceAll("\\(", "");
            cycle = cycle.replaceAll("\\)", "");
            String[] oneGroup = {cycle};
            return oneGroup;
        }

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int pIntIn = wrap(p);
        char pCharIn = _alphabet.toChar(pIntIn);
        char pCharOUt = permute(pCharIn);
        int pIntOut = _alphabet.toInt(pCharOUt);
        return pIntOut;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int cIntIn = wrap(c);
        char cCharIn = _alphabet.toChar(cIntIn);
        char cCharOUt = invert(cCharIn);
        int cIntOut = _alphabet.toInt(cCharOUt);
        return cIntOut;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        char result = p;
        if (_cycles.equals("")) {
            return result;
        } else {
            String[] splitCy = splitCycle(_cycles);
            for (int i = 0; i < splitCy.length; i++) {
                int index = splitCy[i].indexOf(p);
                if (index != -1) {
                    if (index == splitCy[i].length() - 1) {
                        result = splitCy[i].charAt(0);
                    } else {
                        result = splitCy[i].charAt(index + 1);
                    }
                    break;
                }
            }
        }
        return result;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        char result = c;
        if (_cycles.equals("")) {
            return result;
        } else {
            String[] splitCy = splitCycle(_cycles);
            for (int i = 0; i < splitCy.length; i++) {
                int index = splitCy[i].indexOf(c);
                if (index != -1) {
                    if (index == 0) {
                        result = splitCy[i].charAt(splitCy[i].length() - 1);
                    } else {
                        result = splitCy[i].charAt(index - 1);
                    }
                    break;
                }
            }
        }
        return result;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        String[] splitCycle = splitCycle(_cycles);
        for (int i = 0; i < splitCycle.length; i++) {
            if (splitCycle[i].length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Alphabet of this permutation. */
    private String _cycles;
}
