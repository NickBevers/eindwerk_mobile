package be.bluebanana.zakisolver;

import android.content.Context;
import android.util.Log;

import org.paukov.combinatorics3.Generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LetterSolver extends Solver<Character, String> {

    SolverResultCallback<String> resultCallback;

    List<Character> input_list;
    final Set<String> dictionary = new TreeSet<>();

    public LetterSolver() {
        // intentionally left blank
    }

    public void setInput(List<Character> input_list) {
        this.input_list = input_list;
    }

    @Override
    public void solve(SolverResultCallback<String> resultCallback) {
        this.resultCallback = resultCallback;
        this.run();
    }

    @Override
    public void run() {
        TreeSet<String> output = new TreeSet<>(Comparator.comparing(String::length).reversed());

        for (int k=1; k<=input_list.size(); k++) {
            // generate all permutations of the input set of side k and see if we can find them in the dictionary

            Generator.combination(input_list)
                    .simple(k)
                    .stream()
                    .forEach(combination -> Generator.permutation(combination)
                            .simple()
                            .stream()
                            .forEach(characters -> {
                                StringBuilder sb = new StringBuilder();
                                for (Character c: characters) { sb.append(c); }
                                String s = sb.toString();

                                if (dictionary.contains(s))
                                    output.add(sb.toString());
                            }));
        }

        resultCallback.result(output);
    }

    public void loadDictionary(Context context, int resource_id) {
        int i = 0;

        BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resource_id)));
        try {
            while (in.ready()) {
                i++;
                String word = in.readLine().trim();

                // ignore special cases
                if (word.contains(".")) continue;
                if (word.contains(" ")) continue;

                dictionary.add(word);
            }
            Log.d(this.getClass().getName(), String.format("Dictionary loaded; read %d lines.", i));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
