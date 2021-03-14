import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }
    //Uncomment this class once you've created your Palindrome class.


    @Test
    public void testIsPalidrome(){

        assertFalse(palindrome.isPalindrome("fuckyou"));
        assertFalse(palindrome.isPalindrome("sonofbitch"));
        assertFalse(palindrome.isPalindrome("youfuckingidiot"));
        assertTrue(palindrome.isPalindrome("abcdcba"));
        assertTrue(palindrome.isPalindrome("a"));
        assertFalse(palindrome.isPalindrome("ababssba"));
        assertTrue(palindrome.isPalindrome(""));
    }
    @Test
    public void testIsPalidromeOffByOne(){
        CharacterComparator offByOne = new OffByOne();
        assertFalse(palindrome.isPalindrome("fuckyou",offByOne));
        assertFalse(palindrome.isPalindrome("sonofbitch",offByOne));
        assertFalse(palindrome.isPalindrome("youfuckingidiot",offByOne));
        assertTrue(palindrome.isPalindrome("flake",offByOne));
        assertTrue(palindrome.isPalindrome("a",offByOne));
        assertFalse(palindrome.isPalindrome("ababssba",offByOne));
        assertTrue(palindrome.isPalindrome("",offByOne));
    }
}
