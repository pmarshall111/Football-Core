package com.footballbettingcore.utils;

public class ConvertOdds {
    public static double fromFractionToDecimal(String fractionalVal) {
        String[] partsOfFraction = fractionalVal.split("/");
        int numerator = Integer.parseInt(partsOfFraction[0]);
        int denominator = Integer.parseInt(partsOfFraction[1]);
        return fromFractionToDecimal(numerator, denominator);
    }

    public static double fromFractionToDecimal(double numerator, double denominator) {
        return numerator / denominator + 1;
    }

    public static double[] convert3OddsToProbabilities(double[] odds) {
        double[] probabilities = new double[3];
        for (int i = 0; i<odds.length; i++) {
            probabilities[i] = 1/odds[i];
        }
        return probabilities;
    }

    public static double fromProbabilityToOdds(double probability) {
        return 1/probability;
    }

    public static double fromOddsToProbability(double odds) {
        return 1/odds;
    }

}
