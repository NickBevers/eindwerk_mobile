package be.bluebanana.zakisolver;

import org.paukov.combinatorics3.Generator;

import java.util.List;
import java.util.TreeSet;

public class NumberSolver extends Solver<Integer, String> {

    SolverResultCallback<String> resultCallback;

    List<Integer> input_list;
    float target = 0f;
    static float smallest_delta = Float.MAX_VALUE;

    public NumberSolver() {
        // intentionally left blank
    }

    public void setInput(List<Integer> input_list, float target) {

        this.input_list = input_list;
        this.target = target;

    }

    @Override
    public void solve(SolverResultCallback<String> resultCallback) {
        this.resultCallback = resultCallback;
        this.run();
    }

    @Override
    public void run() {
        TreeSet<String> output = new TreeSet<>();

        for (int k=1; k<=input_list.size(); k++) {
            // generate all permutations of the input set of side k and see if they present a solution

            Generator.combination(input_list)
                    .simple(k)
                    .stream()
                    .forEach(combination -> Generator.permutation(combination)
                            .simple()
                            .stream()
                            .forEach(perm -> {
                                List<Node> list = Node.getAllTrees(perm);
                                for (Node root : list) {
                                    try {
                                        float result = root.evaluate();
                                        float delta = Math.abs(result - target);
                                        if ( delta < smallest_delta) {
                                            // better solution, throw away all the rest
                                            output.clear();
                                            smallest_delta = delta;
                                        }
                                        if (delta == smallest_delta) {
                                            output.add(String.format("%s = %.0f", root.toPlainTextString(), result));
                                        }
                                        //Log.d("Node", String.format("%s = %.2f", root.toString(), result));
                                    } catch (ArithmeticException e) {
                                        // ignore divisions by zero...
                                    }
                                }
                            }));
        }

        resultCallback.result(output);
    }

}
