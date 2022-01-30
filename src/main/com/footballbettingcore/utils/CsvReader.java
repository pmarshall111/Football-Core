package com.footballbettingcore.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvReader {
    public static ArrayList<ArrayList<String>> readCsv(String csvPath) {
        ArrayList<ArrayList<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(new ArrayList<>(Arrays.asList(values)));
            }
            return records;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read CSV " + csvPath);
        }
    }
}
