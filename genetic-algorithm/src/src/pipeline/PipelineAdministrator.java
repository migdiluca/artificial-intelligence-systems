package src.pipeline;

import src.models.Alleles;
import src.models.Individual;
import src.pipeline.cutCriterion.CutCriterion;
import src.pipeline.mutation.Mutation;
import src.pipeline.recombination.Recombination;
import src.pipeline.selection.FillAllSelection;
import src.pipeline.selection.Selection;
import src.pipeline.recombination.ConsecutivePairsRecombination;
import src.pipeline.recombination.crossoverFunctions.OnePointCross;
import src.pipeline.cutCriterion.TimeCut;
import src.pipeline.mutation.NoMutation;
import src.pipeline.selection.fitnessFunctions.FitnessFunction;
import src.pipeline.selection.fitnessFunctions.Archer;
import src.pipeline.selection.selectionFunctions.EliteSelection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineAdministrator {
    private final int populationSize;
    private final Alleles alleles;
    private List<Individual> population;

    private final Recombination recombination;
    private final Selection selection;
    private final Mutation mutation;
    private final CutCriterion cutCriterion;
    private final FitnessFunction fitnessFunction;
    private final boolean cloneEnabled;

    private int childrenSize;

    private int generationNumber;
    private List<Double> fitnessHistorial;
    private List<List<Individual>> generations;


    public PipelineAdministrator(int populationSize, int childrenSize, Mutation mutation, Selection selection, FitnessFunction fitnessFunction, Recombination recombination, CutCriterion cutCriterion) throws IOException {
        this.generationNumber = 0;
        this.cloneEnabled = false;
        this.generations = new ArrayList<>();
        this.fitnessHistorial = new ArrayList<>();
        this.populationSize = populationSize;
        this.population = new ArrayList<>(populationSize);
        this.childrenSize = childrenSize;
        String folder = "genetic-algorithm/fulldata/";
        this.alleles = new Alleles(folder, "cascos.tsv", "pecheras.tsv",
                "armas.tsv", "guantes.tsv", "botas.tsv");

        this.recombination = recombination;

        this.fitnessFunction = fitnessFunction;

        this.selection = selection;

        this.mutation = mutation;

        this.cutCriterion = cutCriterion;

        generateRandomPopulation();
    }

    public void generateRandomPopulation(){
        for(int i=0; i<populationSize; i++){
            this.population.add(
                    new Individual(
                            Math.random()*(2 - 1.3) + 1.3,
                            alleles.getHelms().get((int)(Math.random()*alleles.getHelms().size())),
                            alleles.getBreastplates().get((int)(Math.random()*alleles.getBreastplates().size())),
                            alleles.getWeapons().get((int)(Math.random()*alleles.getWeapons().size())),
                            alleles.getGauntlets().get((int)(Math.random()*alleles.getGauntlets().size())),
                            alleles.getBoots().get((int)(Math.random()*alleles.getBoots().size()))
                    )
            );
        }
    }

    public boolean shouldEnd() {
        return cutCriterion.shouldEnd(generationNumber, fitnessHistorial, generations);
    }

    public void step(){
        List<Individual> individuals = this.population;
        individuals = this.recombination.execute(individuals, this.childrenSize);
        individuals = this.mutation.execute(individuals, alleles);
        individuals = this.selection.execute(individuals, populationSize);
        this.population = individuals;
        this.fitnessHistorial.add(getBestFitnessIndividual());
        this.generationNumber++;
        //Clonar elementos y agregar al historial
        if(cloneEnabled)
            generations.add(population.stream().map(Individual::new).collect(Collectors.toCollection(ArrayList::new)));
    }

    public double getBestFitnessIndividual() {
        double bestFitness = -Double.MAX_VALUE;
        for(Individual individual : population) {
            double individualFitness = fitnessFunction.calculate(individual);
            if(individualFitness > bestFitness)
                bestFitness = individualFitness;
        }
        return bestFitness;
    }

    public List<Individual> getPopulation() {
        return population;
    }

    public List<Double> getFitnessHistorial() {
        return fitnessHistorial;
    }
}
