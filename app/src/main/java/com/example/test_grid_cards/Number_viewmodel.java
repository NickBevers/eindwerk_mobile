package com.example.test_grid_cards;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Number_viewmodel extends ViewModel{
    // set the highnumbers list to pick one from later on
    Integer[] highNums = {10, 25, 50, 75, 100};
    public MutableLiveData<ArrayList<Integer>> numberArray;
    ArrayList<Integer> highList = new ArrayList<>(Arrays.asList(highNums));
    MutableLiveData<ArrayList<String>> results = new MutableLiveData<>();

    // see if the number array is empty, and set on if it is empty. otherwise, return the current number array
    public MutableLiveData<ArrayList<Integer>> getNumbers(){
        if (numberArray == null){
            numberArray = new MutableLiveData<>();
            numberArray.setValue(new ArrayList<>());
        }
        return numberArray;
    }

    // pick a number between 1 and 9
    public void pickLowNumber() {
        ArrayList<Integer> list = getNumbers().getValue();
        assert list != null;
        if (list.size() < 6) {
            Random lowr = new Random();
            list.add(lowr.nextInt(9) + 1); //9 is number between 1 and 9
            numberArray.setValue(list);
        }
    }

    // pick a high number from the high number array
    public void pickHighNumber() {
        ArrayList<Integer> list = getNumbers().getValue();
        assert list != null;
        if (list.size() < 6) {
            Random highr = new Random();
            int high = highr.nextInt(highList.size() - 1);
            list.add(highList.get(high));
            numberArray.setValue(list);
        }
    }

    // clear the current numberarray
    public void clearNumber(){
        ArrayList<Integer> list = getNumbers().getValue();
        assert list != null;
        list.clear();
        numberArray.setValue(list);
    }
}
