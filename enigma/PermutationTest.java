package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Han Liu 3034272830 cs61b-agl
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testSplitCyle() {
        String cycle1 = "(ABCDE) (FGHUIJ) (XYZ)";
        String cycle2 = "(ASDFG)";
        Permutation perm1 = new Permutation(cycle1, UPPER);
        Permutation perm2 = new Permutation(cycle2, UPPER);
        assertArrayEquals(new String[]{"ABCDE", "FGHUIJ", "XYZ"},
                Permutation.splitCycle(cycle1));
        assertArrayEquals(new String[]{"ASDFG"},
                Permutation.splitCycle(cycle2));
    }

    @Test
    public void testPermute() {
        perm = new Permutation("(ABCDE) (FGHUIJ) (XYZ)", UPPER);
        assertEquals(0, perm.permute(4));
        assertEquals(3, perm.permute(28));

        assertEquals('A', perm.permute('E'));
        assertEquals('D', perm.permute('C'));

    }

    @Test
    public void testInvert() {
        perm = new Permutation("(ABCDE) (FGHUIJ) (XYZ)", UPPER);
        assertEquals(4, perm.invert(0));
        assertEquals(3, perm.invert(4));

        assertEquals('E', perm.invert('A'));
        assertEquals('C', perm.invert('D'));
    }
}
