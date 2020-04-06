package back.algorithms.util;

import back.AlgorithmSolution;
import back.interfaces.Game;
import back.interfaces.Heuristic;

import java.util.HashSet;
import java.util.Queue;

public class QueueSearch {
    private int expandedNodes;
    private Game gameSolved;
    private HashSet<Game> hashSet;
    private Queue<Game> queue;
    private Heuristic heuristic = null;
    private String name;

    public QueueSearch(String name){
        this.name = name;
    }

    public AlgorithmSolution run(Queue<Game> queue) {
        hashSet = new HashSet<>();
        expandedNodes = 0;
        gameSolved = null;
        this.queue = queue;

        long startTime = System.currentTimeMillis();
        boolean result = queueSearch();
        long endTime = System.currentTimeMillis();

        AlgorithmSolution solution;
        if(result)
            solution = new AlgorithmSolution(this.name, this.expandedNodes, queue.size(), this.gameSolved, endTime - startTime);
        else
            solution = new AlgorithmSolution(this.name, false, this.expandedNodes, endTime - startTime);

        this.heuristic = null;
        return solution;
    }

    public AlgorithmSolution run(Queue<Game> queue, Heuristic heuristic) {
        this.heuristic = heuristic;
        return run(queue);
    }

    public boolean queueSearch() {
        while (!queue.isEmpty()) {
            Game game = queue.poll();

            if(game.gameFinished()){
                this.gameSolved = game;
                return true;
            }

            hashSet.add(game);

            for (Game gameChild : game.calculateChildren()) {
                int estimatedCost = 0;
                if(this.heuristic != null) {
                    estimatedCost = heuristic.evaluate(gameChild);
                    gameChild.setHeuristicValue(estimatedCost);
                }

                if(!hashSet.contains(gameChild) && estimatedCost != Integer.MAX_VALUE)
                    queue.add(gameChild);
            }

            this.expandedNodes++;

        }

        return false;
    }

}