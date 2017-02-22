package de.citec.sc.main;

import de.citec.sc.corpus.QALDCorpus;
import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.evaluator.BagOfLinksEvaluator;
import de.citec.sc.evaluator.ResultEvaluator;

import de.citec.sc.learning.LinkingObjectiveFunction;
import de.citec.sc.qald.QALDCorpusLoader;
import de.citec.sc.query.CandidateRetriever;
import de.citec.sc.query.CandidateRetrieverOnLucene;
import de.citec.sc.query.CandidateRetrieverOnMemory;
import de.citec.sc.query.ManualLexicon;
import de.citec.sc.query.Search;
import de.citec.sc.sampling.StateInitializer;

import de.citec.sc.sampling.SingleNodeExplorer;
import de.citec.sc.template.NodeSimilarityTemplate;
import de.citec.sc.template.QATemplateFactory;

import de.citec.sc.utils.DBpediaEndpoint;
import de.citec.sc.utils.Performance;
import de.citec.sc.utils.ProjectConfiguration;
import de.citec.sc.variable.HiddenVariable;
import de.citec.sc.variable.State;

import de.citec.sc.wordNet.WordNetAnalyzer;
import exceptions.UnkownTemplateRequestedException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import learning.Model;
import learning.Trainer;
import learning.scorer.LinearScorer;
import learning.scorer.Scorer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.similarities.Similarity;
import sampling.Explorer;
import sampling.Initializer;
import sampling.stoppingcriterion.StoppingCriterion;
import templates.AbstractTemplate;

public class Test {

//    private static final Logger log = LogManager.getFormatterLogger();
//
//    public static void run(String[] args, String modelFilePath, int currentEpoch) throws IOException, UnkownTemplateRequestedException, Exception {
//
//        //initialize Search and Manual Lexicon
//        boolean useMatoll = ProjectConfiguration.useMatoll();
//        boolean useManualLexicon = ProjectConfiguration.useManualLexicon();
//
//        Search.useMatoll(useMatoll);
//        ManualLexicon.useManualLexicon(useManualLexicon);
//
//        String datasetName = ProjectConfiguration.getTestDatasetName();
//
//        int maxWords = ProjectConfiguration.getMaxTestWords();
//
//        int numberOfEpochs = ProjectConfiguration.getNumberOfEpochs();
//        int numberOfSamplingSteps = ProjectConfiguration.getNumberOfSamplingSteps();
//        int numberKSamples = ProjectConfiguration.getNumberOfKSamples();
//
//        String wordCountOperator = ProjectConfiguration.getWordCountOperator();
//        boolean sortTrainingDataByWordCount = ProjectConfiguration.orderTrainingData();
//        String retrieverMethod = ProjectConfiguration.getRetrieverMethod();
//
//        log.info("TEST SCRIPT");
//
//        CandidateRetriever retriever = null;
//
//        if (retrieverMethod.equals("memory")) {
//            retriever = new CandidateRetrieverOnMemory("rawIndexFiles/resourceFiles", "rawIndexFiles/classFiles", "rawIndexFiles/predicateFiles", "rawIndexFiles/matollFiles", "rawIndexFiles/matollAdjectiveFiles");
//        } else {
//            retriever = new CandidateRetrieverOnLucene(true, "luceneIndexes/resourceIndex", "luceneIndexes/classIndex", "luceneIndexes/predicateIndex", "luceneIndexes/matollIndex");
//        }
//
//        WordNetAnalyzer wordNet = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
//
//        Search indexLookUp = new Search(retriever, wordNet);
////        Evaluator evaluator = new QueryEvaluator();
//        Evaluator evaluator = new BagOfLinksEvaluator();
//
//        //create dudes
//        HashMap<Integer, String> assignedDUDES = new LinkedHashMap<>();
//
////        assignedDUDES.put(0, "What");
//        assignedDUDES.put(1, "Property");
//        assignedDUDES.put(2, "Individual");
//        assignedDUDES.put(3, "Class");
//        assignedDUDES.put(4, "UnderSpecifiedClass");
////        assignedDUDES.put(5, "Which");
//        assignedDUDES.put(6, "Empty");
////        assignedDUDES.put(7, "Did");
////        assignedDUDES.put(8, "When");
////        assignedDUDES.put(9, "Where");
////        assignedDUDES.put(10, "Who");
//
////        DBpediaEndpoint.setToRemote();
//        LinkingObjectiveFunction objFunction = new LinkingObjectiveFunction(evaluator);
//
//        List<String> validDependencyRelations = new ArrayList<>();
//        validDependencyRelations.add("nsubj");
//        validDependencyRelations.add("nsubjpass");
//        validDependencyRelations.add("nmod");
//        validDependencyRelations.add("nmod:poss");
//        validDependencyRelations.add("dobj");
//        validDependencyRelations.add("amod");
//        validDependencyRelations.add("acl:relcl");
//        validDependencyRelations.add("acl");
//
//        SingleNodeExplorer nodeExplorer = new SingleNodeExplorer(indexLookUp, assignedDUDES);
//
//        List<Explorer<State>> explorers = new ArrayList<>();
//        explorers.add(nodeExplorer);
//
//        /**
//         * load the corpus, corpora
//         */
//        QALDCorpusLoader loader = new QALDCorpusLoader();
//        List<AnnotatedDocument> documents = new ArrayList<>();
//
//        QALDCorpus corpus = loader.load(QALDCorpusLoader.Dataset.valueOf(datasetName));
//
//        //get short text documents
//        for (AnnotatedDocument d1 : corpus.getDocuments()) {
//            String question = d1.getQuestionString();
//
//            if (d1.getQaldInstance().getAggregation().equals("false") && d1.getQaldInstance().getOnlyDBO().equals("true") && d1.getQaldInstance().getHybrid().equals("false")) {
//
//                int numWords = question.split(" ").length;
//
//                //if max word count is equal or higher
//                if (wordCountOperator.equals("equalAndHigher")) {
//                    if (numWords <= maxWords) {
//                        if (DBpediaEndpoint.isValidQuery(d1.getGoldResult(), false)) {
//
//                            documents.add(d1);
//                            d1.getParse().mergeEdges(indexLookUp);
////                        
//                        } else {
//                            System.out.println("Query doesn't run: " + d1.getGoldResult());
//                        }
//                    }
//                } else {
//                    if (numWords == maxWords) {
//                        if (DBpediaEndpoint.isValidQuery(d1.getGoldResult(), false)) {
//
//                            documents.add(d1);
//
//                        } else {
//                            System.out.println("Query doesn't run: " + d1.getGoldResult());
//                        }
//                    }
//                }
//            }
//        }
//
//        List<AnnotatedDocument> test = new ArrayList<>(documents);
//
//        /*
//         * In the following, we setup all necessary components for training
//         * and testing.
//         */
//        /*
//         * Define an objective function that guides the training procedure.
//         */
//        /*
//         * Define templates that are responsible to generate
//         * factors/features to score intermediate, generated states.
//         */
//        List<AbstractTemplate<AnnotatedDocument, State, ?>> templates = new ArrayList<>();
//        try {
//            templates.add(new NodeSimilarityTemplate());
//
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//
//        /*
//         * Create the scorer object that computes a score from the features of a
//         * factor and the weight vectors of the templates.
//         */
//        Scorer scorer = new LinearScorer();
//
//        QATemplateFactory factory = new QATemplateFactory(assignedDUDES);
//
//        File modelDir = new File(modelFilePath);
//        /*
//         * Define a model and provide it with the necessary templates.
//         */
//        Model<AnnotatedDocument, State> model = new Model<>(scorer, templates);
//        model.setMultiThreaded(true);
//        model.loadModelFromDir(modelDir, factory);
//
//        System.out.println(model.toDetailedString());
//
//
//        /*
//         * Create an Initializer that is responsible for providing an initial
//         * state for the sampling chain given a sentence.
//         */
//        Initializer<AnnotatedDocument, State> trainInitializer = new StateInitializer(assignedDUDES);
//
//        StoppingCriterion<State> stopAtMaxModelScore = new StoppingCriterion<State>() {
//
//            @Override
//            public boolean checkCondition(List<State> chain, int step) {
//
//                if (chain.isEmpty()) {
//                    return false;
//                }
//
//                double maxScore = chain.get(chain.size() - 1).getModelScore();
//                int count = 0;
//                final int maxCount = 2;
//
//                for (int i = 0; i < chain.size(); i++) {
//                    if (chain.get(i).getModelScore() >= maxScore) {
//                        count++;
//                    }
//                }
//                return count >= maxCount || step >= numberOfSamplingSteps;
//            }
//        };
//
//        StoppingCriterion<State> modelLinkingCriterion = new StoppingCriterion<State>() {
//
//            @Override
//            public boolean checkCondition(List<State> chain, int step) {
//                if (chain.isEmpty()) {
//                    return false;
//                }
//
//                State lastState = chain.get(chain.size() - 1);
//
//                List<String> lastStateURIs = new ArrayList<>();
//                for (HiddenVariable var : lastState.getHiddenVariables().values()) {
//                    lastStateURIs.add(var.getCandidate().getUri());
//                }
//
//                Evaluator evaluator = new BagOfLinksEvaluator();
//
//                int count = 0;
//                final int maxCount = 2;
//
//                for (int i = chain.size() - 2; i >= 0; i--) {
//                    State chainState = chain.get(i);
//
//                    List<String> chainStateURIs = new ArrayList<>();
//                    for (HiddenVariable var : chainState.getHiddenVariables().values()) {
//                        chainStateURIs.add(var.getCandidate().getUri());
//                    }
//
//                    double sim = evaluator.evaluate(lastStateURIs, chainStateURIs);
//
//                    if (sim == 1.0) {
//                        count++;
//                    }
//                }
//
//                return count >= maxCount || step >= numberOfSamplingSteps;
//            }
//        };
//
//        TopKSampler<AnnotatedDocument, State, String> sampler = new TopKSampler<>(model, scorer, objFunction, explorers, modelLinkingCriterion);
//        sampler.setSamplingStrategy(ListSamplingStrategies.topKBestObjectiveSamplingStrategy(5000));
//        sampler.setTestSamplingStrategy(ListSamplingStrategies.topKModelSamplingStrategy(numberKSamples));
//        sampler.setAcceptStrategy(AcceptStrategies.strictModelAccept());
//
//
//        /*
//         * The trainer will loop over the data and invoke sampling and learning.
//         * Additionally, it can invoke predictions on new data.
//         */
//        Trainer trainer = new Trainer();
//        /*
//         * Same for testdata
//         */
//        List<State> testResults = trainer.test(sampler, trainInitializer, test);
//
//        Map<String, Double> testEvaluation = null;
//
//        String taskName = ProjectConfiguration.getTask();
//        if (taskName.equals("linking")) {
//            testEvaluation = ResultEvaluator.evaluateAllByObjective(testResults, objFunction);
//        } else {
//            //this evaluates by constructing sparql query
//
//        }
//
//        log.info("Evaluation on test data:");
//        testEvaluation.entrySet().forEach(e -> log.info(e));
//
//        Map<String, Double> avrgTest = new LinkedHashMap<>();
//
//        System.out.println("Evaluation on test data:");
//        System.out.println(avrgTest);
//
//        //log the the result
//        if (!testEvaluation.isEmpty()) {
//            String score = testEvaluation.get("Macro F1") + "";
//
//            Performance.logTest(args, score, currentEpoch, testResults, objFunction);
//
//        }
//
//    }
//
//    public static void run(String[] args) throws UnkownTemplateRequestedException, Exception {
//        run(args, "models/model", -1);
//    }
}
