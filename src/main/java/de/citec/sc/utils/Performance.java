/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.utils;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.corpus.SampledMultipleInstance;
import de.citec.sc.learning.LinkingObjectiveFunction;
import de.citec.sc.variable.HiddenVariable;
import de.citec.sc.variable.State;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import learning.ObjectiveFunction;
import static net.ricecode.similarity.StringSimilarityMeasures.score;

/**
 *
 * @author sherzod
 */
public class Performance {

    public static HashMap<String, String> parsedQuestions = new HashMap<>();
    public static HashMap<String, String> unParsedQuestions = new HashMap<>();

    public static void logTrain() {

        String fileName = "Manual_" + ProjectConfiguration.useManualLexicon() + "_Matoll_" + ProjectConfiguration.useMatoll() + "_Dataset_" + ProjectConfiguration.getTrainingDatasetName() + "_Epoch_" + ProjectConfiguration.getNumberOfEpochs() + "_Word_" + ProjectConfiguration.getMaxWordCount();

        //log based on word count
        String p = parsedQuestions.size() + "/" + (parsedQuestions.size() + unParsedQuestions.size()) + "\n\n";
        String u = unParsedQuestions.size() + "/" + (parsedQuestions.size() + unParsedQuestions.size()) + "\n\n";

        for (String p1 : parsedQuestions.keySet()) {
            p += p1 + "\n" + parsedQuestions.get(p1) + "\n\n";
        }
        for (String p1 : unParsedQuestions.keySet()) {
            u += unParsedQuestions.get(p1) + "\n\n";
        }

        String parsedOutputsDirectory = "trainResult";

        File theDir = new File(parsedOutputsDirectory);

        if (!theDir.exists()) {

            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        FileFactory.writeListToFile(parsedOutputsDirectory + "/unParsedInstances_" + fileName + ".txt", u, false);
        FileFactory.writeListToFile(parsedOutputsDirectory + "/parsedInstances_" + fileName + ".txt", p, false);

        unParsedQuestions.clear();
        parsedQuestions.clear();
    }

    public static void logTest(List<SampledMultipleInstance<AnnotatedDocument, String, State>> testResults, ObjectiveFunction function) {

        String fileName = "Manual_" + ProjectConfiguration.useManualLexicon() + "_Matoll_" + ProjectConfiguration.useMatoll() + "_Dataset_" + ProjectConfiguration.getTrainingDatasetName() + "_Epoch_" + ProjectConfiguration.getNumberOfEpochs() + "_Word_" + ProjectConfiguration.getMaxWordCount();

        String correctInstances = "";
        String inCorrectInstances = "";

        double overAllScore = 0;

        int c = 0;
        for (SampledMultipleInstance<AnnotatedDocument, String, State> triple : testResults) {

            double maxScore = 0;
            State maxState = null;
            for (State state : triple.getStates()) {

                double s = function.score(state, triple.getGoldResult());

                if (maxState == null) {
                    maxState = state;
                }

                if (s > maxScore) {
                    maxScore = s;
                    maxState = state;
                }

            }

//            testPrint += maxState + "\nScore: " + maxScore + "\n========================================================================\n";
            overAllScore += maxScore;

            if (maxScore == 1.0) {
                correctInstances += maxState + "\nScore: " + maxScore + "\n========================================================================\n";
                c++;
            } else {
                inCorrectInstances += maxState + "\nScore: " + maxScore + "\n========================================================================\n";
            }
        }

        double MACROF1 = overAllScore / (double) testResults.size();
        correctInstances = c + "/" + testResults.size() + "\nMACRO F1:" + MACROF1 + "\n\n" + correctInstances;
        inCorrectInstances = (testResults.size() - c) + "/" + testResults.size() + "\nMACRO F1:" + MACROF1 + "\n\n" + inCorrectInstances;

        String outputDir = "testResult";

        File theDir = new File(outputDir);

        if (!theDir.exists()) {

            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }
        
        FileFactory.writeListToFile(outputDir+"/parsedInstances_" + fileName + ".txt", correctInstances, false);
        FileFactory.writeListToFile(outputDir+"/unParsedInstances_" + fileName + ".txt", inCorrectInstances, false);
        
        
        System.out.println("Test results\n\nCorrectly predicted: "+c + "/" + testResults.size());
        System.out.println("Incorrectly predicted: "+(testResults.size() - c) + "/" + testResults.size());
        System.out.println("MACRO F1: "+ MACROF1);
    }

    public static void addParsed(String s, String q) {
        if (unParsedQuestions.containsKey(s)) {
            unParsedQuestions.remove(s);
        }

        parsedQuestions.put(s, q);
    }

    public static void addUnParsed(String s, String q) {
        
        //make it un parsed if the parsed map doesn't contain
        //if parsed map contains it means that at some point it parsed, keep it like this
        if(!parsedQuestions.containsKey(s)){
            unParsedQuestions.put(s, q);
        }
    }
}
