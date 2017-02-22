/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.utils;

import de.citec.sc.learning.LinkingObjectiveFunction;
import de.citec.sc.variable.HiddenVariable;
import de.citec.sc.variable.State;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author sherzod
 */
public class Performance {

    public static HashMap<String, String> parsedQuestions = new HashMap<>();
    public static HashMap<String, String> unParsedQuestions = new HashMap<>();

    public static void logTrain(String[] args) {

        String fileName = "Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[1] + "_Epoch_" + args[9] + "_Word_" + args[7] + "_SamplingMethod_" + args[23];

        //log based on word count
        String p = parsedQuestions.size() + "/" + (parsedQuestions.size() + unParsedQuestions.size()) + "\n\n";
        String u = unParsedQuestions.size() + "/" + (parsedQuestions.size() + unParsedQuestions.size()) + "\n\n";

        for (String p1 : parsedQuestions.keySet()) {
            p += p1 + "\n" + parsedQuestions.get(p1) + "\n\n";
        }
        for (String p1 : unParsedQuestions.keySet()) {
            u += p1 + "\n" + unParsedQuestions.get(p1) + "\n\n";
        }

        String parsedOutputsDirectory = "parsedInstanceOutputs";

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

        //log based on all instances
        String allLogFileName = "Train_Instances_Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[1] + "_Epoch_" + args[9] + "_SamplingMethod_" + args[23] + ".txt";

        String trainOutputsDirectory = "parsedInstancesOverall";
        //read all content
        Set<String> oldLogs = FileFactory.readFile(trainOutputsDirectory + "/" + allLogFileName);

        HashMap<String, String> logMap = new LinkedHashMap<>();

        String content = "";

        if (oldLogs.isEmpty()) {
            logMap.put(args[7], "Parsed:" + parsedQuestions.size() + " Unparsed:" + unParsedQuestions.size());
            logMap.put("Total", "Parsed:" + parsedQuestions.size() + " Unparsed:" + unParsedQuestions.size());

        } else {
            int oldParsedCount = 0;
            int oldUnParsedCount = 0;

            int totalParsedCount = 0;
            int totalUnParsedCount = 0;

            for (String o : oldLogs) {
                //read old logs
                if (o.startsWith(args[7])) {

                    String begin = args[7] + "=> Parsed:";
                    String end = " Unparsed:";

                    String z = o.substring(begin.length(), o.indexOf(end));
                    oldParsedCount = Integer.parseInt(z);

                    String z2 = o.substring(o.indexOf(end) + end.length());
                    oldUnParsedCount = Integer.parseInt(z2);
                }
                if (o.startsWith("Total")) {

                    String begin = "Total=> Parsed:";
                    String end = " Unparsed:";

                    String z = o.substring(begin.length(), o.indexOf(end));
                    totalParsedCount = Integer.parseInt(z);

                    String z2 = o.substring(o.indexOf(end) + end.length());
                    totalUnParsedCount = Integer.parseInt(z2);

                    //update the parsed and unparsed numbers
                    totalParsedCount = totalParsedCount - oldParsedCount + parsedQuestions.size();
                    totalUnParsedCount = totalUnParsedCount - oldUnParsedCount + unParsedQuestions.size();

                }
                if (!o.startsWith(args[7]) && !o.startsWith("Total")) {

                    String wordCount = o.substring(0, o.indexOf("=>"));
                    String rest = o.substring(o.indexOf("=> ") + 3);

                    logMap.put(wordCount, rest);
                }
            }

            //add current one
            logMap.put(args[7], "Parsed:" + parsedQuestions.size() + " Unparsed:" + unParsedQuestions.size());

            //create another map to sort
            Map<Integer, String> sortedMap = new TreeMap<Integer, String>();
            for (String s : logMap.keySet()) {
                sortedMap.put(Integer.parseInt(s), logMap.get(s));
            }

            //load logMap again with sorted values
            logMap.clear();
            for (Integer i : sortedMap.keySet()) {
                logMap.put(i.toString(), sortedMap.get(i));
            }

            //add total
            logMap.put("Total", "Parsed:" + totalParsedCount + " Unparsed:" + totalUnParsedCount);
        }

        for (String key : logMap.keySet()) {
            String value = logMap.get(key);

            content += key + "=> " + value;

            if (!key.equals("Total")) {
                content += "\n";
            }
        }

        File trainDir = new File(trainOutputsDirectory);

        if (!trainDir.exists()) {

            try {
                trainDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        //write out all logs to one file
        FileFactory.writeListToFile(trainOutputsDirectory + "/" + allLogFileName, content, false);

        unParsedQuestions.clear();
        parsedQuestions.clear();
    }

    private static void testOutputInstances(List<State> states, String task, LinkingObjectiveFunction function, String fileName) {

        double score = 0.0;

        String output = "";
        for (State s : states) {
            
            output += "\nState : \n" + s.toString();
            
            double v = 10;//function.computeValue(s, s.getDocument().getGoldResult());

            if (task.equals("linking")) {
                
                v = function.computeValue(s, s.getDocument().getGoldQueryString());
                
                String constructedQuery = "";
                
                for (HiddenVariable uriVar : s.getHiddenVariables().values()) {
                    if(uriVar.getCandidate().getUri().equals("EMPTY_STRING")){
                        continue;
                    }
                    constructedQuery += uriVar.getCandidate().getUri()+ "\n";
                }
                output += "\nConstructed Query : \n" + constructedQuery.trim();
                
            } else {

                String questionString = s.getDocument().getQuestionString();

                boolean askQuery = false;

                if (questionString.startsWith("Did") || questionString.startsWith("Does") || questionString.startsWith("Is") || questionString.startsWith("Was") || questionString.startsWith("Are")) {
                    askQuery = true;
                }
            }

            

            output += "\nGold Query : \n" + s.getInstance().getGoldQueryString();

            output += "\nQuestion : \n" + s.getInstance().getQuestionString();

            output += "\nScore: " + v;
            output += "\n========================================================================\n";

            score += v;
        }

        score = score / (double) states.size();

        output += "\n\nMacro F1 : " + score;

        FileFactory.writeListToFile(fileName, output, false);
    }

    public static void logTest(String[] args, String score, int currentEpoch, List<State> states, LinkingObjectiveFunction function) {

        String instanceFileName = "";
        String instanceOverallFileName = "";
        String instanceOutputDirectory = "";
        String instanceOutputsOverallDirectory = "";

        if (currentEpoch != -1) {
            instanceFileName = "Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[19] + "_Epoch_" + args[9] + "_Word_" + args[31] + "_FeatureLevel_" + args[21] + "_SamplingMethod_" + args[23] + "_Sorted_" + args[27] + "_Task_" + args[33] + "_CurrentEpoch_" + currentEpoch + ".txt";
            instanceOverallFileName = "Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[19] + "_Epoch_" + args[9] + "_Word_" + args[31] + "_FeatureLevel_" + args[21] + "_SamplingMethod_" + args[23] + "_Sorted_" + args[27] + "_Task_" + args[33] + ".txt";

            instanceOutputDirectory = "trainingEpochInstances";
            instanceOutputsOverallDirectory = "trainingEpochOverall";

        } else {
            instanceFileName = "Test_Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[19] + "_Epoch_" + args[9] + "_Word_" + args[31] + "_FeatureLevel_" + args[21] + "_SamplingMethod_" + args[23] + "_Sorted_" + args[27] + "_Task_" + args[33] + ".txt";
            instanceOverallFileName = "Test_Manual_" + args[3] + "_Matoll_" + args[5] + "_Dataset_" + args[19] + "_Epoch_" + args[9] + "_Word_" + args[31] + "_FeatureLevel_" + args[21] + "_SamplingMethod_" + args[23] + "_Sorted_" + args[27] + "_Task_" + args[33] + ".txt";
            instanceOutputDirectory = "testingInstances";
            instanceOutputsOverallDirectory = "testingOverall";
        }

        File instanceDirectory = new File(instanceOutputDirectory);

        if (!instanceDirectory.exists()) {

            try {
                instanceDirectory.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        File overallDirectory = new File(instanceOutputsOverallDirectory);

        if (!overallDirectory.exists()) {

            try {
                overallDirectory.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        //output instance
        testOutputInstances(states, args[33], function, instanceOutputDirectory + "/" + instanceFileName);

        String line = "";
        String newLine = "";

        if (currentEpoch == -1) {
            line = "Word: " + args[31] + "  Manual: " + args[3] + "  Matoll: " + args[5] + "  Dataset: " + args[19] + "  TopK: " + args[17] + "  Epochs: " + args[9] + "  SamplingSteps: " + args[11] + " Sampling Method: " + args[23] + " Feature Level: " + args[21] + "_Task_" + args[33];
            newLine = line + " === Score : " + score;
        } else {
            line = "Current Epoch\t" + currentEpoch;
            newLine = line + "\tScore\t" + score;
        }

        Set<String> oldLogs = FileFactory.readFile(instanceOutputsOverallDirectory + "/" + instanceOverallFileName);

        //go through all old logs
        //update the old one if exists
        List<String> list = new ArrayList<>();
        for (String o : oldLogs) {
            if (o.contains(line)) {
                list.add(newLine);
            } else {
                list.add(o);
            }
        }

        if (!list.contains(newLine)) {
            list.add(newLine);
        }

        Collections.sort(list);

        String content = "";

        for (String n : list) {
            content += n + "\n";
        }

        content = content.trim();

        //write out all logs to one file
        FileFactory.writeListToFile(instanceOutputsOverallDirectory + "/" + instanceOverallFileName, content, false);

    }

    public static void addParsed(String s, String q) {
        if (unParsedQuestions.containsKey(s)) {
            unParsedQuestions.remove(s);
        }

        parsedQuestions.put(s, q);
    }

    public static void addUnParsed(String s, String q) {
        if (parsedQuestions.containsKey(s)) {
            parsedQuestions.remove(s);
        }

        unParsedQuestions.put(s, q);
    }
}
