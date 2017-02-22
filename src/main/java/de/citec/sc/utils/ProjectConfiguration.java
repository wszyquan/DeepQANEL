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
    private static final String PARAM_SETTING_MAX_WORDS = "-w";
    private static final String PARAM_SETTING_EPOCHS = "-e";
    private static final String PARAM_SETTING_SAMPLING_STEPS = "-s";
    private static final String PARAM_SETTING_LEARNER = "-l";
    private static final String PARAM_SETTING_EVALUATOR = "-v";
    private static final String PARAM_SETTING_TOP_K = "-k";
    private static final String PARAM_SETTING_FEATURE_LEVEL = "-f";
    private static final String PARAM_SETTING_SAMPLING_SCORE = "-p";
    private static final String PARAM_SETTING_WORD_COUNT_OPERATOR = "-t";
    private static final String PARAM_SETTING_SORT_TRAINING_DATA_BY_WORD_COUNT = "-g";
    private static final String PARAM_SETTING_RETRIEVER_METHOD = "-c";
    private static final String PARAM_SETTING_MAX_WORDS_TEST = "-q";
    private static final String PARAM_SETTING_TASK = "-o";
    
    public static String getTask() {

        return PARAMETERS.get(PARAM_SETTING_TASK);
    }
    
    public static String getRetrieverMethod() {

        return PARAMETERS.get(PARAM_SETTING_RETRIEVER_METHOD);
    }

    public static boolean orderTrainingData() {

        String g= PARAMETERS.get(PARAM_SETTING_SORT_TRAINING_DATA_BY_WORD_COUNT);
        return g.equals("true");
    }
    public static String getWordCountOperator() {

        return PARAMETERS.get(PARAM_SETTING_WORD_COUNT_OPERATOR);
    }
    public static String getSamplingMethod() {

        return PARAMETERS.get(PARAM_SETTING_SAMPLING_SCORE);
    }
    public static String getLearner() {

        return PARAMETERS.get(PARAM_SETTING_LEARNER);
    }
    public static String getFeatureLevel() {

        return PARAMETERS.get(PARAM_SETTING_FEATURE_LEVEL);
    }
    
    public static String getDatasetName() {

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

    public static String getEvaluatorName() {
        String evaluatorName = PARAMETERS.get(PARAM_SETTING_EVALUATOR);

        return evaluatorName;
    }

    public static int getMaxTestWords() {
        int maxWords = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_MAX_WORDS_TEST));
        return maxWords;
    }
    
    public static int getMaxWords() {
        int maxWords = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_MAX_WORDS));
        return maxWords;
    }

    public static int getNumberOfEpochs() {
        int numberOfEpochs = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_EPOCHS));

        return numberOfEpochs;
    }

    public static int getNumberOfSamplingSteps() {
        int numberOfSamplingSteps = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_SAMPLING_STEPS));

        return numberOfSamplingSteps;
    }

    public static int getNumberOfKSamples() {
        int numberKSamples = Integer.parseInt(PARAMETERS.get(PARAM_SETTING_TOP_K));

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
