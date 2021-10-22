package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Han Liu 3034272830 cs61b-agl
 */
class Alphabet {
    /** Additional field needed. */
    private String _chars;

    /** Additional field needed. */
    private char[] cArray;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;

        cArray = new char[_chars.length()];
        for (int i = 0; i < _chars.length(); i++) {
            cArray[i] = _chars.charAt(i);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return this._chars.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return 'A' <= ch && ch <= 'Z';
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= 0 && index < this.size()) {
            return cArray[index];
        } else {
            throw new Error("character number INDEX out of the alphabet!!");
        }

    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int index = _chars.indexOf(ch);
        if (index == -1) {
            throw new Error("character out of the alphabet range!!");
        } else {
            return index;
        }
    }

}
