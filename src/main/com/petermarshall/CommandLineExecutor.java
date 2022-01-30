package com.petermarshall;

import com.petermarshall.scrape.SofaScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.lang.Runtime.getRuntime;

public class CommandLineExecutor {
    private static final Logger logger = LogManager.getLogger(CommandLineExecutor.class);

    public static void runCommand(String command) {
        Process p = null;
        try {
            p = getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line, errorLine = null;
            while ((line=reader.readLine())!=null || (errorLine=errorReader.readLine())!=null) {
                if (line != null) logger.info(line.trim());
                if (errorLine != null) logger.error(errorLine.trim());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (p != null) p.destroy();
        }
    }

    public static void main(String[] args) {
        runCommand("octave /home/peter/Documents/personalProjects/Octave-Football-Training/FootballTrainingProbabilitiesScorePredict.m /home/peter/Documents/personalProjects/Octave-Football-Training/all_thetas.csv /tmp/matches_to_predict.csv /tmp/predictions.csv");
    }
}
