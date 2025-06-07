# Evil Hangman
An advanced twist on the classic Hangman game that cheats. Normally, the computer picks a single word and accurately represents it as the player tries to guess all of the letters in the word. In Evil Hangman, the computer instead maintains a list of every word in the English language, then continuously pares down the word list to try to dodge the player's guesses as much as possible.

## Features

- Dictionary-based dynamic word selection
- Fair interface but evil backend logic
- Tracks letters guessed, remaining guesses, and current word pattern

## How to Play

1. Choose the word length, number of guesses, and difficulty level
2. Begin guessing letters one at a time
3. After each guess:
   - You’ll see whether the letter was “correct"
   - The known word pattern will update
   - The game adjusts the hidden word behind the scenes to avoid helping you
4. Win if you guess the entire word before running out of guesses
5. Lose if your guesses run out first, then the program finally picks a word to reveal
