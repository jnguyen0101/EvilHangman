import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {
    
    // a char to represent an unrevealed letter
    private static final char EMPTY = '-';

    // instance variables / fields
    private Map<Integer, ArrayList<String>> words;
    private ArrayList<String> currentWords;
    private int numGuesses;  
    private ArrayList<Character> guessesMade;
    private String currentPattern;
    private int guessNum;
    private int familySize;
    private HangmanDifficulty difficulty;
    private boolean hardness;
    private boolean debugOn;

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        // check preconditions
        if (words == null || words.size() <= 0) {
            throw new IllegalArgumentException("words may not be null or empty.");
        }
        
        // adds the words to a map organized by length of words and saves debug option
        this.words = new TreeMap<>();
        this.debugOn = debugOn;
        for (String word : words) {
            int length = word.length();
            if (!this.words.containsKey(length)) {
                this.words.put(length, new ArrayList<>());
            }
            this.words.get(length).add(word);
        }
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        // calls other constructor but sets debugOn to false
        this(words, false);
    }


    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length) {
        // make sure that the length exists in the dictionary
        if (words.containsKey(length)) {
            return words.get(length).size();
        }
        return 0;
    }


    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * @param wordLen the length of the word to pick this time.
     * numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     * player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        // check preconditions
        if (wordLen <= 0 || numGuesses < 1) {
            throw new IllegalArgumentException("wordLen has to be greater than 0, " +
                    " numGuesses has to be greater than or equal to 1");
        }
        
        this.numGuesses = numGuesses;
        difficulty = diff;
        guessNum = 1;
        guessesMade = new ArrayList<>();
        currentWords = new ArrayList<>();
        // creates the empty pattern of the word based on length input by user
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLen; i++) {
            sb.append(EMPTY);
        }
        currentPattern = sb.toString();
        // adds the words that match the length to current words list
        currentWords = words.get(wordLen);
    }


    /**
     * The number of words still possible (live) based on the guesses so far.
     *  Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        if (currentWords == null) {
            return 0;
        }
        return currentWords.size();
    }


    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return numGuesses;
    }


    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade() {
        // sorts the list in alphabetical order and returns it as a String
        Collections.sort(guessesMade);
        return guessesMade.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        return guessesMade.contains(guess);
    }


    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character 
     * for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return currentPattern;
    }


    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
        // check preconditions
        if (alreadyGuessed(guess)) {
            throw new IllegalStateException("This letter has already been guessed.");
        }
        
        // creates new maps to store patterns, word families, and number of words in
        // each word family, adds guess to the guesses made
        TreeMap<String, ArrayList<String>> families = new TreeMap<>();
        TreeMap<String, Integer> result = new TreeMap<>();
        guessesMade.add(guess);
        
        // adds patterns and their word families
        for (String word : currentWords) {
            String pattern = makePattern(word, guess);
            // if the pattern doesn't already exist, create a new mapping
            // and add the pattern to the map and word to its list, else
            // add the word to its respective pattern's list
            if (!families.containsKey(pattern)) {
                families.put(pattern, new ArrayList<String>());
                families.get(pattern).add(word);
                result.put(pattern, 1);
            } else {
                families.get(pattern).add(word);
                result.put(pattern, result.get(pattern) + 1);
            }
        }
        // updates the pattern and the list of current words that match the pattern
        familySize = families.size();
        currentPattern = patternUpdater(result);
        currentWords = families.get(currentPattern);
        guessNum++;
        // decrements the guesses that are left if the pattern didn't use the character
        if (currentPattern.indexOf(guess) < 0) {
            numGuesses--;
        }
        // prints out debugging text if debug is on
        if (debugOn) {
            printDebug();
        }
        return result;
    }


    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        if (numWordsCurrent() == 0) {
            throw new IllegalStateException("There are no words available.");
        }
        
        // gets a random word from the current word list; if there is only one
        // word, random will always be 0 and will return that one word
        int random = (int) (Math.random() * currentWords.size());
        return currentWords.get(random);
    }
    
    // helper methods
    
    /**
     * creates the pattern of a word based on a character
     * @param word the word to create pattern
     * @param c the character to fill in spots
     * @return a String of the pattern created
     */
    private String makePattern(String word, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                // adds the character if it matches character in word
                sb.append(c);
            } else if (currentPattern.charAt(i) != EMPTY){
                // adds other characters based on current pattern
                sb.append(word.charAt(i));
            } else {
                // adds "-" if character doesn't match
                sb.append(EMPTY);
            }
        }
        return sb.toString();
    }
    
    /**
     * updates the current pattern based on the difficulty
     * @param map the TreeMap that contains the patterns and the
     * amount of word families that correspond with that pattern
     * @return a String of the current pattern
     */
    private String patternUpdater(TreeMap<String, Integer> map) {
        final int EASY = 2;
        final int MEDIUM = 4;
        // checks the guess number if it is an even round for easy or the fourth
        // round for medium to use second hardest word family
        if ((difficulty == HangmanDifficulty.EASY && guessNum % EASY == 0) ||
                (difficulty == HangmanDifficulty.MEDIUM && guessNum % MEDIUM == 0)) {
            hardness = false;
            return secondHardestPat(map);
        } 
        // returns hardest word family if difficulty is hard or if the guess
        // number does not meet the criteria of second hardest word family
        // for easy or medium
        hardness = true;
        return hardestPat(map);
    }

    /**
     * gets the amount of revealed letters in a pattern
     * @param pat the String pattern to count revealed letters
     * @return an int that represents the amount of revealed letters
     * in the pattern
     */
    private int revealedLetCount(String pat) {
        // searches the word for characters that are revealed
        int result = 0;
        for (int i = 0; i < pat.length(); i++) {
            if (pat.charAt(i) != EMPTY) {
                result++;
            }
        }
        return result;
    }
    
    /**
     * gets a list of the patterns that have the least revealed letters
     * @param pats the list of patterns to search for revealed letters
     * @return an ArrayList of Strings that contains the patterns with the
     * least amount of revealed letters
     */
    private ArrayList<String> minRevealed(ArrayList<String> pats) {
        ArrayList<String> mins = new ArrayList<>();
        int least = Integer.MAX_VALUE;
        // finds the least amount of revealed letters
        for (int i = 0; i < pats.size(); i++) {
            if (revealedLetCount(pats.get(i)) < least) {
                least = revealedLetCount(pats.get(i));
            }
        }
        // adds all words that are equal to the least amount to a list
        for (String s : pats) {
            if (revealedLetCount(s) == least) {
                mins.add(s);
            }
        }
        return mins;
    }

    /**
     * gets the pattern of the hardest word family
     * @param map the TreeMap to search for the hardest word family
     * @return a String pattern of the hardest word family
     */
    private String hardestPat(TreeMap<String, Integer> map) {
        // finds the most amount of words in a word family
        int most = 0;
        for (Map.Entry<String, Integer> ent: map.entrySet()) {
            if (ent.getValue() > most) {
                most = ent.getValue();
            }
        }
        // adds the patterns that equal the most to a list
        ArrayList<String> pats = new ArrayList<>();
        for (Map.Entry<String, Integer> ent: map.entrySet()) {
            if (ent.getValue() == most) {
                pats.add(ent.getKey());
            }
        }
        // return pattern if there are no ties
        if (pats.size() == 1) {
            return pats.get(0);
        }
        // return pattern based on least amount of revealed letters
        if (minRevealed(pats).size() == 1) {
            return minRevealed(pats).get(0);
        }
        // return pattern based on lexicographical ordering
        ArrayList<String> lex = minRevealed(pats);
        String smallest = pats.get(0);
        for (String s : lex) {
            if (s.compareTo(smallest) < 0) {
                smallest = s;
            }
        }
        return smallest;
    }
    
    /**
     * gets the pattern of the second hardest word family
     * @param map the TreeMap to search for the second hardest word family
     * @return a String pattern of the second hardest word family
     */
    private String secondHardestPat(TreeMap<String, Integer> map) {
        // if there is only one family left, do not remove anything
        if (familySize == 1) {
            return hardestPat(map);
        }
        // else remove the hardest word to get the second hardest word
        TreeMap<String, Integer> removedHardest = new TreeMap<>();
        removedHardest.putAll(map);
        removedHardest.remove(hardestPat(map));
        return hardestPat(removedHardest);
    }

    /**
     * prints text for debugging
     */
    private void printDebug() {
        if (hardness) {
            // word family is the hardest
            System.out.println("\nDEBUGGING: Picking hardest list.");
        } else {
            if (familySize != 1) {
                // word family is second hardest, more than 1 family
                System.out.println("\nDEBUGGING: Difficulty second hardest "
                        + "pattern and list.");
            } else {
                // word family is second hardest but only 1 family
                System.out.println("\nDEBUGGING: Should pick second hardest "
                        + "pattern this turn, but only one pattern available.");
                System.out.println("\nDEBUGGING: Picking hardest list.");
            }
        }
        System.out.println("DEBUGGING: New pattern is: " + currentPattern +
                ". New family has " + currentWords.size() + " words.\n");
    }
}
