import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByOne {

    // You must use this CharacterComparator and not instantiate
    // new ones, or the autograder might be upset.
    static CharacterComparator offByOne = new OffByOne();
    @Test
    public void testOffByOne(){

        assertTrue(offByOne.equalChars('a','b'));
        assertFalse(offByOne.equalChars('a','a'));
        assertFalse(offByOne.equalChars('a','d'));
        assertFalse(offByOne.equalChars('e','b'));
        assertTrue(offByOne.equalChars('c','b'));
    }
    // Your tests go here.
//    Uncomment this class once you've created your CharacterComparator interface and OffByOne class. **/
}
