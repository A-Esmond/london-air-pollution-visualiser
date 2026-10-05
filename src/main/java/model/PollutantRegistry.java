package model;

import model.dataFilteringOptions.DataFilters;

import java.util.*;

/**
 * @author E. N. and T. A.
 */
public class PollutantRegistry {
    private final Map<String, List<DataSet>> pollutants;

    public PollutantRegistry() {
        pollutants = new HashMap<>();
    }

    public void registerPollutant(DataSet dataSet) {
        dataSet = DataFilters.isOnMap().filterData(dataSet);
        dataSet = DataFilters.isValidValue().filterData(dataSet);
        if (!pollutants.containsKey(dataSet.getPollutant())) {
            pollutants.put(dataSet.getPollutant(), new ArrayList<>());
        }
        pollutants.get(dataSet.getPollutant()).add(dataSet);
    }

    public Set<String> getPollutantNames() {
        return pollutants.keySet();
    }

    public List<DataSet> getPollutant(String name) {
        return pollutants.get(name);
    }

    public List<DataSet> getPollutant(String name, int startYear, int endYear) {
        return pollutants.get(name.toLowerCase()).stream()
                .filter(dataSet -> startYear <= Integer.parseInt(dataSet.getYear()) &&
                        Integer.parseInt(dataSet.getYear()) <= endYear)
                .toList();
    }

    public Integer getPollutantMinYear(String name) {
        int minYear = Integer.MAX_VALUE;
        List<DataSet> pollutantData = pollutants.get(name);
        for (DataSet dataSet : pollutantData) {
            if (dataSet.getYear() == null || Integer.parseInt(dataSet.getYear()) < minYear) {
                minYear = Integer.parseInt(dataSet.getYear());
            }
        }
        return minYear;
    }

    public Integer getPollutantMaxYear(String name) {
        int maxYear = Integer.MIN_VALUE;
        List<DataSet> pollutantData = pollutants.get(name);
        for (DataSet dataSet : pollutantData) {
            if (dataSet.getYear() == null || Integer.parseInt(dataSet.getYear()) > maxYear) {
                maxYear = Integer.parseInt(dataSet.getYear());
            }
        }
        return maxYear;
    }

    public List<DataSet> getAllDatasets() {
        List<DataSet> dataSets = new ArrayList<>();

        for (List<DataSet> dataset : pollutants.values()) {
            dataSets.addAll(dataset);
        }

        return dataSets;
    }

    //Move this to welcome panel
    public String getTotalYears() {
        HashSet<Integer> years = new HashSet<>();
        for (String name : getPollutantNames()) {
            years.add(getPollutantMaxYear(name));
            years.add(getPollutantMinYear(name));
        }
        int minYear = Collections.min(years);
        int maxYear = Collections.max(years);
        return minYear + "-" + maxYear;
    }

    public Set<String> getYearsCovered() {
        return new HashSet<>(
                getAllDatasets()
                        .stream()
                        .map(DataSet::getYear).toList());
    }

    public DataSet lookForDataset(String pollutantSelected, String yearSelected) {
        if (pollutantSelected == null || yearSelected == null) {
            throw new IllegalArgumentException("Pollutant and year must not be null");
        }

        List<DataSet> dataSets = pollutants.get(pollutantSelected);
        if (dataSets == null) {
            throw new IllegalArgumentException("Unknown pollutant: " + pollutantSelected);
        }

        for (DataSet dataSet : dataSets) {
            if (yearSelected.equals(dataSet.getYear())) {
                return dataSet;
            }
        }

        return null;
    }
}
