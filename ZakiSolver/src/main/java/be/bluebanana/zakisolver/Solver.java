package be.bluebanana.zakisolver;

import java.util.Collection;

public abstract class Solver<T1, T2> implements Runnable {

    public interface SolverResultCallback<T> {
        void result(Collection<T> results);
    }

}
