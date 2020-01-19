/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    /* added by me */
    private static final String ourTag = "GACS_AS";
    private static final ArrayList<String> wordList = new ArrayList();
    private static final HashSet<String> wordSet = new HashSet();
    private static final HashMap<String, ArrayList<String>> lettersToWord = new HashMap<String, ArrayList<String>>();


    /* read in file and return ArrayList wordList of values */
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordSet.add(word);
            wordList.add(word);
            String sortedWord = sortLetters(word);
            ArrayList<String> tmpList = new ArrayList();
            tmpList.add(word);
            if (lettersToWord.containsKey(sortedWord)) {
                tmpList.addAll(lettersToWord.get(sortedWord));
            }
            lettersToWord.put(sortedWord, tmpList);
        }
        Log.d(ourTag, String.format("wordList size: %d", wordList.size()));
        Log.d(ourTag, String.format("wordSet size: %d", wordSet.size()));
        Log.d(ourTag, String.format("lettersToWord size: %d", lettersToWord.size()));
    }

    /* checks that given word is in dict and it does NOT have the base word in it:
    *   e.g. "STOPS" contains "STOP" so that's not a good anagram
    *       but "POST" OR "SPOT" have the same letters but are different words (good) */
    public boolean isGoodWord(String word, String base) {
        // we start assuming it's a good bad, if it passes our tests below we'll make it good
        boolean isGood = true;
        // first let's check that the new word is in the dictionary (wordSet)
        if ( wordSet.contains(word) ) {
            // ok it's in the dictionary - now check to see if it contains the base
            if ( word.toLowerCase().contains(base.toLowerCase()) ) {
                Log.d(ourTag, String.format("'%s' contains base '%s'", word, base));
                isGood = false;
            }
        } else {
            Log.d(ourTag, String.format("'%s' is not in the dictionary", word));
            isGood = false;
        }
        return isGood;
    }

    /* sorts the letters in a string */
    public String sortLetters(String word) {

        // convert input string to char array
        char tempArray[] = word.toCharArray();

        // sort tempArray
        Arrays.sort(tempArray);
        String sortedLetters = new String(tempArray);

        // return new sorted string
        return sortedLetters;
    }

    /* Creates a list of all possible anagrams of a given word */
    public List<String> getAnagrams(String targetWord) {
        // alphabetize the characters in the target word
        String sortedTarget = sortLetters(targetWord);

        // find all words in dictionary that are rearranged versions of targetword
        ArrayList<String> anagramList = (ArrayList<String>) lettersToWord.get(sortedTarget);

      /* way only using ArrayList:
        for( String word : wordList ) {
            if(word.length() == targetWord.length() && sortLetters(word).equals(sortedTarget)) {
                anagramList.add(word);
            }
        }
      */

        return anagramList;
    }

    /* Creates list of all possible words from base word + 1 letter */
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        String biggerWord;
        ArrayList<String> result = new ArrayList<String>();
        char letterInAlphabet;
        for(int i = 0; i < 26; i++) {
            letterInAlphabet = (char)('a'+i);
            Log.d(ourTag, String.format("Trying: %s", letterInAlphabet));

            // ok, so for each letter of the alphabet we:
            //  ? add the letter to word and run getAnagrams?
            biggerWord = word + letterInAlphabet;
            Log.d(ourTag, String.format("   trying: %s plus %c", word, letterInAlphabet));
            List anagramsOfBiggerWord = getAnagrams(biggerWord);
            if( anagramsOfBiggerWord != null ) {
                result.addAll(anagramsOfBiggerWord);
            }
        }
        for ( String anagram : result) {
            Log.d(ourTag, anagram);
        }
        return result;
    }

    /* Randomly picks word from dict with at least X number of anagrams */
    public String pickGoodStarterWord() {
        int minAnagrams = 5;
        int numAnagrams = 0;
        String goodStarterWord = new String();

        while(numAnagrams <= minAnagrams) {
            List<String> anagrams;
            int rando = (int) (Math.random() * wordList.size());
            anagrams = getAnagrams(wordList.get(rando));
            numAnagrams = anagrams.size();
            goodStarterWord = wordList.get(rando);
        }

        return goodStarterWord;
    }
}
