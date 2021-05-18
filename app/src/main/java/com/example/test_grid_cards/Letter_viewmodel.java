package com.example.test_grid_cards;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Letter_viewmodel extends AndroidViewModel {
    // ↓ set all variables needed
    public MutableLiveData<ArrayList<Character>> letterArray;
    public String randomWord = "TESTER";
    InputStream is;
    int randomLetter = 26; // choose a number that corresponds with  letter of the alphabet
    int numberToAdd = 97; // number to add to get lowercase a in ASCII table
    int minWordLength = 2; // there are no valid words with less then 2 characters

    public Letter_viewmodel(@NonNull Application application) {
        super(application);
    }

    // check if the letterarray contains any characters, and return it
    public MutableLiveData<ArrayList<Character>> getLetters(){
        if (letterArray == null){
            letterArray = new MutableLiveData<ArrayList<Character>>();
            letterArray.setValue(new ArrayList<Character>());
        }
        return letterArray;
    }

    // pick a random letter from an ascii table
    public char pickALetter() {
        Random random = new Random();
        int ascii = random.nextInt(randomLetter) + numberToAdd;; // lowercase 'a'
        return (char)ascii;
    }

    // see if the letter it picked is a vowel
    public boolean isVowel (char c) {
        char[] vowels = {'a', 'e', 'i', 'o', 'u'};

        for (char v: vowels) {
            if (v == c) return true;
        }
        return false;
    }

    // see if the letter it picked is a consonant
    public boolean isConsonant (char c) {
        return !isVowel(c);
    }


    // function for picking a vowel to add to letterarray
    public void pickVowel() {
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        if (list.size() < 6){
            char c;
            do {
                c = pickALetter();
            } while (!isVowel(c));

            list.add(c);
            letterArray.setValue(list);
        }
    }

    // function for picking a consonant to add to letterarray
    public void pickConsonant() {
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        if (list.size() < 6){
            char c;
            do {
                c = pickALetter();
            } while (!isConsonant(c));
            list.add(c);
            //Log.d("TAG", "pickConsonant: LISTLIST" + list.size());
            letterArray.setValue(list);
        }
    }

    // empty the letterarray
    public void clearLetter(){
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        list.clear();
        letterArray.setValue(list);
    }

    //check the given string to make sure it exists, and can be formed with the characters from the letterarray
    public boolean checkText(String userText, boolean res) {
        try {
            // a word must contani at least 2 letters, if not it isn't a word
            if (userText.length() < minWordLength) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplication().getApplicationContext(), getApplication().getApplicationContext().getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show());
                //Log.i("TAG", "TOOO SHORT");
                res = false;
            }
            else{
                //use wordlist to see how many letters from the letterarray are in the given string
                char [] wordArray = userText.toCharArray();
                ArrayList<Character> tempList = letterArray.getValue();

                ArrayList<Character> wordList = new ArrayList<>();
                for (char c:wordArray){
                    wordList.add(c);
                }
                assert tempList != null;
                wordList.retainAll(tempList);

                //use the correct file to check the string for realness (depending on how long it is)
                int fileToOpen = getApplication().getResources().getIdentifier("raw/filter" + String.valueOf(userText.length()), null, getApplication().getApplicationContext().getPackageName());

                is = this.getApplication().getApplicationContext().getResources().openRawResource(fileToOpen);
                // if the string contains letters not from the letterarray, it isn't valid
                if (wordList.size() < userText.length()){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplication().getApplicationContext(), getApplication().getApplicationContext().getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show());
                    res = false;
                }

                // check the file with words for the string to see if it exists
                byte[] buffer = new byte[is.available()];
                while (is.read(buffer) != -1){
                    String jsontext = new String(buffer);
                    res = jsontext.contains(userText);
                }
            }

        } catch (Exception e) {
            Log.e("TAG", "" + e.toString());
        }
        return res;
    }
}
