public class Palindrome {

    public Deque<Character> wordToDeque(String word){
        LinkedListDeque<Character> LLD=new LinkedListDeque<>();
        for (int i=0;i<word.length();i++){
            LLD.addLast(word.charAt(i));
        }
        return LLD;
    }
    public boolean isPalindrome(String word, CharacterComparator cc){
        Deque<Character>LLD=wordToDeque(word);
        while (LLD.size()>1){
            if (!cc.equalChars(LLD.getFirst(),LLD.getLast()))
                return false;
            LLD.removeFirst();
            LLD.removeLast();
        }
        return true;
    }
    public boolean isPalindrome(String word){
        Deque<Character> LLD=wordToDeque(word);
        while (LLD.size()>1){
            if (LLD.getFirst()!=LLD.getLast())

                return false;
            LLD.removeFirst();
            LLD.removeLast();
        }
        return true;
    }
}
