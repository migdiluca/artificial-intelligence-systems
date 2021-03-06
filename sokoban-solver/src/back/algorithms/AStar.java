package back.algorithms;

import back.AlgorithmSolution;
import back.algorithms.util.SearchAlgorithm;
import back.algorithms.util.SearchCollection;
import back.interfaces.Algorithm;
import back.interfaces.Game;
import back.interfaces.Heuristic;
import back.interfaces.InformedAlgorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStar implements InformedAlgorithm {

    private Heuristic heuristic;

    public AStar(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public AStar(){

    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    @Override
    public String getName(){
        return "A*";
    }

    @Override
    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public AlgorithmSolution run(Game game) {
        Comparator<Game> gameComparator = (g1, g2) -> {
            float f1 = g1.getHeuristicValue() + g1.getCostValue();
            float f2 = g2.getHeuristicValue() + g2.getCostValue();
            return Float.compare(f1, f2);
        };

        SearchAlgorithm searchAlgorithm = new SearchAlgorithm(this.getName());
        return searchAlgorithm.run(game, gameComparator, heuristic);
    }
}
