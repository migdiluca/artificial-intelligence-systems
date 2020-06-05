package kohonen;

import utils.CsvReader;
import utils.Vector;

import java.io.IOException;

import java.util.Map;
import java.util.function.Supplier;

public class Simulation {

    public static void run(){
        int numberOfIterations = 300000;

        //Kohonen map generator
        int latticeDim = 3;
        int inputDim = 7;
        KohonenMap map = (new KohonenMap.Builder())
                .setLattice(new SquareLattice(cellGenerator(inputDim), latticeDim))
                .setNeighborhoodFunction(Simulation::neighborhoodFunction)
                .setLearningRateFunction(Simulation::learningRateFunction)
                .setTimeConstant(numberOfIterations/Math.log(2))
                .create();

        //Read europe.csv for training
        Map<String, Vector> categories;
        try{
            categories = (new CsvReader(
                    "./unsupervised-learning/europe.csv",
                    ",",
                    true)
            )
                    .read()
                    .standardizeValues()
                    .getCategories();

        }catch (IOException e){
            e.printStackTrace();
            return;
        }


        //Training
        System.out.println("\nStarting training");
        for(int t=0; t< numberOfIterations; t++){
            for(Vector vector : categories.values()){
                map.step(vector);
            }
        }

        //Done training
        System.out.println("\nDone training!");

        //Creating a matrix to classify input values into the matrix coordinates
        FrequencyMatrix frequencyMatrix = new FrequencyMatrix(latticeDim);
        for(String key : categories.keySet()){
            Vector vector = categories.get(key);

            Lattice.Coord coord = map.activate(vector);
            frequencyMatrix.addMember(coord.getI(), coord.getJ(), key);
        }
        
        //Print frequency of categories inside each cell
        System.out.println("\nNumber of members on each cell:");
        for(int i=0; i<latticeDim; i++){
            for(int j=0; j<latticeDim; j++){
                System.out.print(frequencyMatrix.getCellPopulation(i, j) + "");
                System.out.print(" ");
            }
            System.out.print("\n");
        }

        //Print members of each coordinates
        System.out.println("\nMembers on each coordinate (i, j):");
        for(int i=0; i<latticeDim; i++){
            for(int j=0; j<latticeDim; j++){
                System.out.print("(" + i + ", " + j +"): " + frequencyMatrix.getCellMembers(i, j) + "");
                System.out.print("\n");
            }
        }

    }

    public static double neighborhoodFunction(int time, double timeConstant){
        double r0 = 3;
        return r0*Math.exp(-time/timeConstant);
    }

    public static double learningRateFunction(int time, double timeConstant){
        double a0 = 1;
        return a0*Math.exp(-time/timeConstant);
    }

    public static Supplier<Cell> cellGenerator(int dim){
        return () -> {
            double[] values = new double[dim];
            for(int i=0; i<dim; i++){
                values[i] = Math.random();
            }
            return new CellImpl(new Vector(values));
        };
    }
}
