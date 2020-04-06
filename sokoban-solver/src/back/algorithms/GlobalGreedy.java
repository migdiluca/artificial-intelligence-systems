package back.algorithms;

import back.AlgorithmSolution;
import back.algorithms.util.QueueSearch;
import back.interfaces.Game;
import back.interfaces.Heuristic;
import back.interfaces.InformedAlgorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

public class GlobalGreedy implements InformedAlgorithm {

    private Heuristic heuristic;

    public GlobalGreedy(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public GlobalGreedy(){

    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    @Override
    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public AlgorithmSolution run(Game game) {
        Comparator<Game> gameComparator = Comparator.comparingInt(Game::getHeuristicValue);
        PriorityQueue<Game> gamesPriorityQueue = new PriorityQueue<>(gameComparator);

        game.setHeuristicValue(heuristic.evaluate(game));
        gamesPriorityQueue.add(game);

        QueueSearch queueSearch = new QueueSearch(this.getName());
        return queueSearch.run(gamesPriorityQueue, heuristic);
    }

    @Override
    public String getName(){
        return "Global Greedy";
    }
}
