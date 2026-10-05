package core;

import model.DataSet;
import model.PollutantRegistry;
import util.DataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and registration of pollution datasets.
 *
 * Responsible for initialising all available datasets from file paths
 * and storing them within a PollutantRegistry. Acts as the central
 * access point for dataset management in the application.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class DataHandler {

    private final PollutantRegistry pollutantRegistry;

    /**
     * Constructs a DataHandler and loads all datasets.
     */
    public DataHandler() {
        this.pollutantRegistry = new PollutantRegistry();
        registerDatasets();
    }

    /**
     * Loads datasets from file paths and registers them
     * in the PollutantRegistry.
     */
    private void registerDatasets() {
        DataLoader loader = new DataLoader();

        getFilePaths()
                .stream()
                .map(loader::loadDataFile)
                .forEach(pollutantRegistry::registerPollutant);
    }

    /**
     * Provides the list of dataset file paths.
     *
     * @return A list of file paths for all datasets
     */
    private List<String> getFilePaths() {
        List<String> paths = new ArrayList<>();

        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22023.csv");
        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22022.csv");
        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22021.csv");
        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22020.csv");
        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22019.csv");
        paths.add("/src/resources/UKAirPollutionData/NO2/mapno22018.csv");

        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102018g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102019g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102020g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102021g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102022g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm10/mappm102023g.csv");

        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252018g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252019g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252020g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252021g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252022g.csv");
        paths.add("/src/resources/UKAirPollutionData/pm2.5/mappm252023g.csv");

        return paths;
    }

    /**
     * Returns the pollutant registry containing all loaded datasets.
     *
     * @return The PollutantRegistry
     */
    public PollutantRegistry getPollutantRegistry() {
        return pollutantRegistry;
    }
}