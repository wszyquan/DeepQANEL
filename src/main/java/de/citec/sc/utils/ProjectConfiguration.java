/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sherzod
 */
public class ProjectConfiguration {

    public static void loadConfigurations(String[] args) {

        //read parameters
        readParamsFromCommandLine(args);

    }

    private static final Map<String, String> PARAMETERS = new HashMap<>();

    private static final String PARAMETER_PREFIX = "-";
    private static final String PARAM_SETTING_DATASET = "-d1";
    private static final String PARAM_SETTING_TEST_DATASET = "-d2";
    private static final String PARAM_SETTING_MANUAL_LEXICON = "-m1";
    private static final String PARAM_SETTING_MATOLL = "-m2";
    private static final String PARAM_SETTING_EPOCHS = "-e";
    private static final String PARAM_SETTING_SAMPLING_STEPS = "-s";
    private static final String PARAM_SETTING_BEAMSIZE_TRAINING = "-k";
    private static final String PARAM_SETTING_BEAMSIZE_TEST = "-l";
    private static final String PARAM_SETTING_TASK = "-t";
    private static final String PARAM_SETTING_MAX_WORD_COUNT = "-w";

    public static String getTask() {

        return PARAMETERS.get(PARAM_SETTING_TASK);
    }

    public static String getTrainingDatasetName() {

        return PARAMETERS.get(PARAM_SETTING_DATASET);
    }

    public static String getTestDatasetName() {

        return PARAMETERS.get(PARAM_SETTING_TEST_DATASET);
    }

    public static boolean useManualLexicon() {

        boolean useManualLexicon = "true".equals(PARAMETERS.get(PARAM_SETTING_MANUAL_LEXICON));

        return useManualLexicon;
    }

    public static boolean useMatoll() {

        boolean useMatoll = "true".equals(PARAMETERS.get(PARAM_SETTING_MATOLL));

        return useMatoll;
    }

    public static int getNumberOfEpochs() {
        int numberOfEpochs = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_EPOCHS));

        return numberOfEpochs;
    }
    public static int getMaxWordCount() {
        int numberOfEpochs = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_MAX_WORD_COUNT));

        return numberOfEpochs;
    }

    public static int getNumberOfSamplingSteps() {
        int numberOfSamplingSteps = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_SAMPLING_STEPS));

        return numberOfSamplingSteps;
    }

    public static int getTrainingBeamSize() {
        int numberKSamples = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_BEAMSIZE_TRAINING));

        return numberKSamples;
    }
    public static int getTestBeamSize() {
        int numberKSamples = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_BEAMSIZE_TEST));

        return numberKSamples;
    }

    private static void readParamsFromCommandLine(String[] args) {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith(PARAMETER_PREFIX)) {
                    PARAMETERS.put(args[i], args[i++ + 1]); // Skip value
                }
            }
        }
    }
}
