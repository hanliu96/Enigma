package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

import static enigma.EnigmaException.*;


/** Enigma simulator.
 *  @author Han Liu 3034272830 cs61b-agl
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);
        _configCopy = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        boolean firline = true;
        _machine = readConfig();
        while (_input.hasNextLine()) {
            _msgInput = _input.nextLine();
            if (firline && _msgInput.charAt(0) != '*') {
                throw error("Input file error, NO config!");
            }
            firline = false;

            if (_msgInput.equals("")) {
                _msgTrans = "";
                printMessageLine(_msgTrans);
            } else if (_msgInput.charAt(0) == '*') {
                _configLine = _msgInput;
                strArray = _configLine.split(" ");
                if (strArray.length <= _numRotors + 1) {
                    throw error("Input file error, WRONG number of rotors!");
                }
                checkStr(strArray);
                checkStr2(strArray);
                rts = new String[_numRotors];
                System.arraycopy(strArray, 1,  rts, 0,  _numRotors);
                _machine.insertRotors(rts);
                _initialSet = strArray[_numRotors + 1];
                if (strArray.length > _numRotors + 2
                        && strArray[_numRotors + 2].charAt(0) != '(') {
                    _ringSet = strArray[_numRotors + 2];
                    setUp(_machine, _initialSet, _ringSet);
                } else {
                    setUp(_machine, _initialSet);
                }
                if (_configLine.contains("(")) {
                    int index = _configLine.indexOf("(");
                    _cyclesPlug = _configLine.substring(index);
                } else {
                    _cyclesPlug = "";
                }
                _permPlug = new Permutation(_cyclesPlug, _alphabet);
                _machine.setPlugboard(_permPlug);
            } else {
                _msgTrans = _machine.convert(_msgInput);
                printMessageLine(_msgTrans);
            }
        }
    }

    /** Helper method to organize each config line and make sure every rotor has
     *  correct configuration using STRARR. */
    private void checkStr(String[] strArr) {
        boolean isReflector = false;
        boolean isNotMvRotor = false;
        boolean isNotFixed = false;

        for (int i = 0; i < _reflector.size(); i++) {
            if (strArr[1].equals(_reflector.get(i))) {
                isReflector = true;
            }
        }
        if (!isReflector) {
            throw error("First rotor is not a reflector, ERROR!");
        }
        outerloop:
        for (int i = 2; i < (_numRotors - _numPawls - 1) + 2; i++) {
            for (String s1 : _mvRotors) {
                if (strArr[i].equals(s1)) {
                    isNotFixed = true;
                    break outerloop;
                }
            }
            for (String s2 : _reflector) {
                if (strArr[i].equals(s2)) {
                    isNotFixed = true;
                    break outerloop;
                }
            }
        }
        if (isNotFixed) {
            throw error("Fixed rotor is not correct!");
        }
        outerloop2:
        for (int i = (_numRotors - _numPawls + 1); i < _numRotors + 1; i++) {
            for (String s1 : _reflector) {
                if (strArr[i].equals(s1)) {
                    isNotMvRotor = true;
                    break outerloop2;
                }
            }
            for (String s2 : _nonMvRotors) {
                if (strArr[i].equals(s2)) {
                    isNotMvRotor = true;
                    break outerloop2;
                }
            }
        }
        if (isNotMvRotor) {
            throw error("Moving rotor arguments are not correct!");
        }
    }

    /** Helper method to organize each config line and make sure every rotor has
     *  correct configuration using STRARR. */
    private void checkStr2(String[] strArr) {
        boolean isMissNamed = false;
        boolean isRepeated = false;
        for (int i = 1; i < _numRotors + 1; i++) {
            for (int j = i + 1; j < _numRotors + 1; j++) {
                if (strArr[i].equals(strArr[j])) {
                    isRepeated = true;
                    break;
                }
            }
        }
        if (isRepeated) {
            throw error("A rotor is repeated in the setting line! "
                    + "Please modify the INPUT file!");
        }

        for (int i = 1; i < _numRotors + 1; i++) {
            if (_reflector.indexOf(strArr[i]) == -1
                    && _nonMvRotors.indexOf(strArr[i]) == -1
                    && _mvRotors.indexOf(strArr[i]) == -1) {
                isMissNamed = true;
                break;
            }
        }
        if (isMissNamed) {
            throw error("At least one of these rotors is misnamed! "
                    + "Please modify the INPUT file!");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.nextLine();
            _alphabet = new Alphabet(alpha);
            _numRotors = _config.nextInt();
            _numPawls = _config.nextInt();

            rotorInfo();
            ArrayList<Rotor> arrayRotors = new ArrayList<>();
            for (int i = 0; i < _array2D.length; i++) {
                arrayRotors.add(readRotor(_array2D[i]));
            }
            return new Machine(_alphabet, _numRotors, _numPawls, arrayRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Helper method to return a 2-D array containing information about every
     * rotor in the conf file. */
    private void rotorInfo() {
        _config.nextLine();
        lenConf();
        _configCopy.nextLine();
        _configCopy.nextLine();

        String[][] array2D = new String[_lenConf][1];
        for (int i = 0; i < _lenConf; i++) {
            array2D[i][0] = _configCopy.nextLine();
        }
        String[][] splitArray2D = new String[_lenConf][2];
        for (int j = 0; j < _lenConf; j++) {
            splitArray2D[j] = split(array2D[j]);
        }

        for (int k = 0; k < _lenConf; k++) {
            String left = splitArray2D[k][0];
            left = left.replaceAll(" ", "");
            if (left.length() == 0) {
                splitArray2D[k - 1][1] = splitArray2D[k - 1][1]
                        + " " + splitArray2D[k][1];
            }
        }

        ArrayList<String> L = new ArrayList<>();
        ArrayList<String> R = new ArrayList<>();
        for (int m = 0; m < _lenConf; m++) {
            String left1 = splitArray2D[m][0];
            left1 = left1.replaceAll(" ", "");
            if (left1.length() != 0) {
                L.add(splitArray2D[m][0]);
                R.add(splitArray2D[m][1]);
            }
        }
        int size = L.size() + R.size();
        String[][] result = new String[size / 2][2];
        for (int n = 0; n < size / 2; n++) {
            result[n][0] = L.get(n);
            result[n][1] = R.get(n);
        }
        _array2D = result;
    }

    /** Split each line in config file into two part, one containing
     *  rotor's name, type and notches, the other containing permutation,
     *  return a string array using ARR. */
    private String[] split(String[] arr) {
        int index = arr[0].indexOf("(");
        String left = arr[0].substring(0, index);
        String right = arr[0].substring(index);
        String[] result = {left, right};
        return result;
    }

    /** Return the number of lines containing information about each rotor. */
    private void lenConf() {
        int len = 0;
        while (_config.hasNextLine()) {
            len += 1;
            _config.nextLine();
        }
        _lenConf = len;
    }

    /** Return a rotor, reading its description from _config using
     *  ROTORSTRARRAY. */
    private Rotor readRotor(String[] rotorStrArray) {
        try {
            String cycles = rotorStrArray[1];
            String nameOfRotor;
            String notch;
            String rotorNameNotch = rotorStrArray[0].trim();
            String[] splitNameNotch = rotorNameNotch.split(" ");
            Map<Character, Character> parenRL = new HashMap<>();
            LinkedList<Character> paren = new LinkedList<>();
            parenRL.put(')', '(');
            if (!noOpenParen(cycles, paren, parenRL)) {
                throw error("Permutation format is not correct!"
                        + "Please check input file!");
            }

            Permutation permutation = new Permutation(cycles, _alphabet);
            nameOfRotor = splitNameNotch[0];
            if (splitNameNotch[1].charAt(0) == 'R') {
                _reflector.add(nameOfRotor);
                return new Reflector(nameOfRotor, permutation);
            } else if (splitNameNotch[1].charAt(0) == 'N') {
                _nonMvRotors.add(nameOfRotor);
                return new FixedRotor(nameOfRotor, permutation);
            } else {
                _mvRotors.add(nameOfRotor);
                notch = splitNameNotch[1].substring(1);
                return new MovingRotor(nameOfRotor, permutation, notch);
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Return true if STR has any PAREN left single, from Map PARENRL
     *  to make sure permutation will be in correct format. */
    private boolean noOpenParen(final String str,
                                final LinkedList<Character> paren,
                                final Map<Character, Character> parenRL) {
        if (str == null || str.isEmpty()) {
            return paren.isEmpty();
        } else if (parenRL.containsValue(str.charAt(0))) {
            paren.add(str.charAt(0));
            return noOpenParen(str.substring(1), paren, parenRL);
        } else if (parenRL.containsKey(str.charAt(0))) {
            if (paren.getLast() == parenRL.get(str.charAt(0))) {
                paren.removeLast();
                return noOpenParen(str.substring(1), paren, parenRL);
            } else {
                return false;
            }

        } else {
            return noOpenParen(str.substring(1), paren, parenRL);
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Set M according to the specification given on SETTINGS, RINGSTELLUNG
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings, String ringStellung) {
        M.setRotors(settings, ringStellung);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if (i % 6 == 0) {
                msg = msg.substring(0, i) + " "
                        + msg.substring(i, msg.length());
            }
        }
        _msgTrans = msg.trim();
        _output.println(_msgTrans);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Additional field needed. */
    private Machine _machine;

    /** Additional field needed. */
    private int _numRotors;

    /** Additional field needed. */
    private int _numPawls;

    /** Additional field needed. */
    private Scanner _configCopy;

    /** Additional field needed. */
    private int _lenConf;

    /** Additional field needed. */
    private String[][] _array2D;

    /** Additional field needed. */
    private ArrayList<String> _reflector = new ArrayList<>();

    /** Additional field needed. */
    private ArrayList<String> _nonMvRotors = new ArrayList<>();

    /** Additional field needed. */
    private ArrayList<String> _mvRotors = new ArrayList<>();

    /** Additional field needed. */
    private String _msgTrans;

    /** Additional field needed. */
    private String[] strArray;

    /** Additional field needed. */
    private String[] rts;

    /** Additional field needed. */
    private String _initialSet;

    /** Additional field needed. */
    private String _cyclesPlug;

    /** Additional field needed. */
    private Permutation _permPlug;

    /** Additional field needed. */
    private String _msgInput;

    /** Additional field needed. */
    private String _configLine;

    /** Additional field needed. */
    private String _ringSet;









}
