package de.citec.sc.main;

import de.citec.sc.corpus.QALDCorpus;
import de.citec.sc.corpus.AnnotatedDocument;

import de.citec.sc.evaluator.BagOfLinksEvaluator;

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
import de.citec.sc.utils.DBpediaEndpoint;

import de.citec.sc.utils.Performance;
import de.citec.sc.utils.ProjectConfiguration;
import de.citec.sc.variable.HiddenVariable;
import de.citec.sc.variable.State;

import de.citec.sc.wordNet.WordNetAnalyzer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import learning.DefaultLearner;
import learning.Learner;
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

public class Train {

//    private static final Logger log = LogManager.getFormatterLogger();
//
//    public static void run(String[] args) throws IOException {
//
//        //initialize Search and Manual Lexicon
//        boolean useMatoll = ProjectConfiguration.useMatoll();
//        boolean useManualLexicon = ProjectConfiguration.useManualLexicon();
//
//        Search.useMatoll(useMatoll);
//        ManualLexicon.useManualLexicon(useManualLexicon);
//
//        String datasetName = ProjectConfiguration.getDatasetName();
//
//        int maxWords = ProjectConfiguration.getMaxWords();
//
//        int numberOfEpochs = ProjectConfiguration.getNumberOfEpochs();
//        int numberOfSamplingSteps = ProjectConfiguration.getNumberOfSamplingSteps();
//        int numberKSamples = ProjectConfiguration.getNumberOfKSamples();
//
//        String evaluatorName = ProjectConfiguration.getEvaluatorName();
//        String samplingMethod = ProjectConfiguration.getSamplingMethod();
//
//        String wordCountOperator = ProjectConfiguration.getWordCountOperator();
//        boolean sortTrainingDataByWordCount = ProjectConfiguration.orderTrainingData();
//        String retrieverMethod = ProjectConfiguration.getRetrieverMethod();
//
//        log.info("START");
//
//        log.info("\nMax tokens : " + maxWords + "\n");
//
//        System.out.println("\nMax tokens : " + maxWords + "\n");
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
//
//        Evaluator evaluator = new BagOfLinksEvaluator();
//
//        //get stop word list for Explorer, not to query words like "the, is, have ..."
//        //create dudes
//        HashMap<Integer, String> assignedDUDES = new LinkedHashMap<>();
//
////
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
////        assignedDUDES.put(11, "Is_Copula");
////        assignedDUDES.put(12, "How");
//
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
//        validDependencyRelations.add("xcomp");
//        //explorers
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
//                            d1.getParse().mergeEdges(indexLookUp);
//                            documents.add(d1);
//
//                        } else {
//                            System.out.println("Query doesn't run: " + d1.getGoldResult());
//                        }
//                    }
//                } else {
//                    if (numWords == maxWords) {
//                        if (DBpediaEndpoint.isValidQuery(d1.getGoldResult(), false)) {
//
//                            documents.add(d1);
////                        
//                        } else {
//                            System.out.println("Query doesn't run: " + d1.getGoldResult());
//                        }
//                    }
//                }
//            }
//        }
//
//        if (sortTrainingDataByWordCount) {
//            Collections.sort(documents);
//        } //else shuffle documents
//        else {
//            Collections.shuffle(documents);
//        }
//
//        List<AnnotatedDocument> train = new ArrayList<>(documents);
//
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
//            System.exit(1);
//        }
//
//        /*
//         * Create the scorer object that computes a score from the features of a
//         * factor and the weight vectors of the templates.
//         */
//        Scorer scorer = new LinearScorer();
//
//        /*
//         * Define a model and provide it with the necessary templates.
//         */
//        Model<AnnotatedDocument, State> model = new Model<>(scorer, templates);
//        model.setMultiThreaded(true);
//
//
//        /*
//         * Create an Initializer that is responsible for providing an initial
//         * state for the sampling chain given a sentence.
//         */
//        Initializer<AnnotatedDocument, State> trainInitializer = new StateInitializer(assignedDUDES);
//
//        /*
//         * Define the explorers that will provide "neighboring" states given a
//         * starting state. The sampler will select one of these states as a
//         * successor state and, thus, perform the sampling procedure.
//         */
//        StoppingCriterion<State> objectiveOneCriterion = new StoppingCriterion<State>() {
//
//            @Override
//            public boolean checkCondition(List<State> chain, int step) {
//                if (chain.isEmpty()) {
//                    return false;
//                }
//
//                double maxScore = chain.get(chain.size() - 1).getObjectiveScore();
//
//                if (maxScore == 1.0) {
//                    return true;
//                }
//
//                int count = 0;
//                final int maxCount = 4;
//
//                for (int i = 0; i < chain.size(); i++) {
//                    if (chain.get(i).getObjectiveScore() >= maxScore) {
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
//        TopKSampler<AnnotatedDocument, State, String> sampler = new TopKSampler<>(model, scorer, objFunction, explorers, objectiveOneCriterion);
//        sampler.setSamplingStrategy(ListSamplingStrategies.topBestObjectiveSamplingStrategy());
//        sampler.setAcceptStrategy(AcceptStrategies.objectiveAccept());
//
//        /*
//         * Define a learning strategy. The learner will receive state pairs
//         * which can be used to update the models parameters.
//         */
//        Learner learner = new DefaultLearner<>(model, 0.1);
//
//
//        /*
//         * The trainer will loop over the data and invoke sampling and learning.
//         * Additionally, it can invoke predictions on new data.
//         */
//        Trainer trainer = new Trainer();
//        trainer.addEpochCallback(new EpochCallback() {
//
//            @Override
//            public void onStartEpoch(Trainer caller, int epoch, int numberOfEpochs, int numberOfInstances) {
//
//                if (samplingMethod.equals("objective")) {
//                    sampler.setSamplingStrategy(ListSamplingStrategies.topKBestObjectiveSamplingStrategy(numberKSamples));
//                    sampler.setAcceptStrategy(AcceptStrategies.objectiveAccept());
//                    sampler.setStoppingCriterion(objectiveOneCriterion);
//                    System.out.println("Trained model:\n" + model.toDetailedString());
//                    log.info("Switched to Objective Score");
//                } //model score ranking
//                else if (samplingMethod.equals("model")) {
//                    sampler.setSamplingStrategy(ListSamplingStrategies.topKModelSamplingStrategy(numberKSamples));
//                    sampler.setAcceptStrategy(AcceptStrategies.strictModelAccept());
//                    sampler.setStoppingCriterion(modelLinkingCriterion);
//                    System.out.println("Trained model:\n" + model.toDetailedString());
//                    log.info("Switched to Model Score");
//                } //hybrid
//                else {
//                    if (epoch % 2 == 0) {
//                        sampler.setSamplingStrategy(ListSamplingStrategies.topKBestObjectiveSamplingStrategy(numberKSamples));
//                        sampler.setAcceptStrategy(AcceptStrategies.objectiveAccept());
//                        sampler.setStoppingCriterion(objectiveOneCriterion);
//                        System.out.println("Trained model:\n" + model.toDetailedString());
//                        log.info("Switched to Objective Score");
//                    } else {
//                        sampler.setSamplingStrategy(ListSamplingStrategies.topKModelSamplingStrategy(numberKSamples));
//                        sampler.setAcceptStrategy(AcceptStrategies.strictModelAccept());
//                        sampler.setStoppingCriterion(modelLinkingCriterion);
//                        System.out.println("Trained model:\n" + model.toDetailedString());
//                        log.info("Switched to Model Score");
//                    }
//                }
//
////                EpochCallback.super.onStartEpoch(caller, epoch, numberOfEpochs, numberOfInstances); //To change body of generated methods, choose Tools | Templates.
//            }
//
//        });
//
//        trainer.addEpochCallback(new EpochCallback() {
//
//            @Override
//            public void onEndEpoch(Trainer caller, int epoch, int numberOfEpochs, int numberOfInstances) {
//
////                try {
////                    String modelFilePath = "models/model_epoch_" + epoch;
////                    
////                    model.saveModelToFile("models", "model_epoch_" + epoch);
////                    
////                    System.out.println("Epoch # "+epoch+" ended. Testing the model");
////                    
////                    //test all levels of features
////                    args[21] = "low-medium-high";//low, medium , high, low-medium
////                    
////                    Test.run(args, modelFilePath, epoch);
////
////                    
////                    args[21] = "medium-high";//low, medium , high, low-medium
////                    
////                    Test.run(args, modelFilePath, epoch);
////                    
////                    args[21] = "low-high";//low, medium , high, low-medium
////                    
////                    Test.run(args, modelFilePath, epoch);
////                    
////                    args[21] = "low-medium";//low, medium , high, low-medium
////                    
////                    Test.run(args, modelFilePath, epoch);
////                    
////                    
////                    
////                    
////                } catch (IOException ex) {
////                    java.util.logging.Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
////                } catch (Exception ex) {
////                    java.util.logging.Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
////                }
//            }
//
//        });
//
//        trainer.train(sampler, trainInitializer, learner, train, numberOfEpochs);
//
//        model.saveModelToFile("models", "model");
//        System.out.println(model.toDetailedString());
//        log.info("\nModel: \n" + model.toDetailedString() + "\n");
//
//        Performance.logTrain(args);
//    }
}
