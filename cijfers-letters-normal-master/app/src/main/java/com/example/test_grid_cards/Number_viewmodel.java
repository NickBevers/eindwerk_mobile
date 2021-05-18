package com.example.test_grid_cards;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kotlin.jvm.Synchronized;

public class Number_viewmodel extends ViewModel{
    // set the highnumbers list to pick one from later on
    public MutableLiveData<ArrayList<Integer>> numberArray;
    Integer[] highNums = {10, 25, 50, 75, 100};
    ArrayList highList = new ArrayList<Integer>(Arrays.asList(highNums));

    // see if the number array is empty, and set on if it is empty. otherwise, return the current number array
    public MutableLiveData<ArrayList<Integer>> getNumbers(){
        if (numberArray == null){
            numberArray = new MutableLiveData<ArrayList<Integer>>();
            numberArray.setValue(new ArrayList<Integer>());
        }
        return numberArray;
    }

    // pick a number between 1 and 9
    public void pickLowNumber() {
        ArrayList<Integer> list = getNumbers().getValue();
        assert list != null;
        if (list.size() < 6) {
            Random lowr = new Random();
            //if (list.)
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
            list.add((Integer) highList.get(high));
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
