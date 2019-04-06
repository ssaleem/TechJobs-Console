package org.launchcode.techjobs.console;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "resources/job_data.csv";
    private static Boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<String>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }
//        sort results alphabetically (A-Z comes before a-z)
        Collections.sort(values);

        return values;
    }

    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();

        return deepCloneJobs();
    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of the field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        // case-insensitive matching
        value = value.toLowerCase();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {

            String aValue = row.get(column);

            if (aValue.toLowerCase().contains(value)) {
                jobs.add(row);
            }
        }

        return jobs;
    }

    /**
     * Returns results of searching the jobs data by value, value is
     * searched in all columns
     * @param value Value to search for across all columns
     * @return List of all jobs matching the value in any field/column
     */

    public static ArrayList<HashMap<String, String>> findByValue(String value){

        // load data, if not already loaded
        loadData();

        // case-insensitive matching
        value = value.toLowerCase();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();
        for(HashMap<String, String> job: allJobs){

            //  iterate through each column of a particular job
            for(String jobFieldValue: job.values()){

                if(jobFieldValue.toLowerCase().contains(value)){
                    jobs.add(job);

                    //  break loop to avoid adding same row again in
                    //  case there is value match in multiple columns
                    break;
                }
            }
        }

        return jobs;
    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

    /**
     * Creates a deep copy of allJobs static field to prevent
     * modification from any consumer class
     * deep copy: recursively copy objects until you reach immutable or
     * primitive types
     * @return a List containing deep clone of allJobs static field
     */

    private static ArrayList<HashMap<String, String>> deepCloneJobs(){

        // create new list of job entries
        ArrayList<HashMap<String, String>> jobsCopy = new ArrayList<HashMap<String, String>>();

        for(HashMap<String, String> job: allJobs){

            // create new job entry to store immutable strings
            HashMap<String, String> jobCopy = new HashMap<>();
            for(Map.Entry<String, String> jobFieldEntry: job.entrySet()){
                jobCopy.put(jobFieldEntry.getKey(), jobFieldEntry.getValue());
            }
            jobsCopy.add(jobCopy);
        }
        return jobsCopy;
    }


}
