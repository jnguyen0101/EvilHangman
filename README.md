# Evil Hangman
Evil Hangman is an assignment in which students write a computer program that cheats at the classic game Hangman. Normally, when writing a computer to play Hangman, the computer picks a single word and accurately represents it as the human player tries to guess all of the letters in the word. In Evil Hangman, the computer instead maintains a list of every word in the English language, then continuously pares down the word list to try to dodge the player's guesses as much as possible.

The algorithm that drives Evil Hangman is fairly straightfoward. The computer begins by maintaining a list of all words in the English language of a particular length. Whenever the player guesses, the computer partitions the words into "word families" based on the positions of the guessed letters in the words. For example, if the word list is ECHO, HEAL, BEST, and LAZY and the player guesses the letter 'E', then there would be three word families:

* E---, containing ECHO.
* -E--, containing HEAL and BEST.
* ----, containing LAZY.

Once the words are partitioned into equivalence classes, the computer can pick the largest of these classes to use as its remaining word list. It then reveals the letters in the positions indicated by the word family. In this case, the computer would pick the family -E-- and would reveal an E in the second position of the word.
