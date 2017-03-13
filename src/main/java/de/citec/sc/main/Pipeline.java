package de.citec.sc.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.corpus.SampledMultipleInstance;
import de.citec.sc.learning.LinkingObjectiveFunction;
import de.citec.sc.learning.NELHybridSamplingStrategyCallback;
import de.citec.sc.learning.NELTrainer;
import de.citec.sc.sampling.MyBeamSearchSampler;
import de.citec.sc.sampling.SingleNodeExplorer;
import de.citec.sc.sampling.StateInitializer;
import de.citec.sc.template.LexicalTemplate;
import de.citec.sc.template.QATemplateFactory;
import de.citec.sc.utils.Performance;
import de.citec.sc.utils.ProjectConfiguration;
import de.citec.sc.variable.State;
import exceptions.UnkownTemplateRequestedException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import learning.AdvancedLearner;
import learning.Learner;
import learning.Model;
import learning.ObjectiveFunction;
import learning.optimizer.SGD;
import learning.scorer.DefaultScorer;
import learning.scorer.Scorer;
import sampling.Explorer;
import sampling.samplingstrategies.AcceptStrategies;
import sampling.samplingstrategies.BeamSearchSamplingStrategies;
import sampling.stoppingcriterion.BeamSearchStoppingCriterion;
import templates.AbstractTemplate;

/**
 *
 * @author sjebbara
 */
public class Pipeline {

    private static int NUMBER_OF_SAMPLING_STEPS = 15;
    private static int NUMBER_OF_EPOCHS = 6;
    private static int BEAM_SIZE_TRAINING = 20;
    private static int BEAM_SIZE_TEST = 500;
    private static Logger log = LogManager.getFormatterLogger();

    private static Set<String> validPOSTags;
    private static Map<Integer, String> semanticTypes;
    private static Map<Integer, String> specialSemanticTypes;
    private static Set<String> frequentWordsToExclude;

    public static void initialize(Set<String> v, Map<Integer, String> s, Map<Integer, String> st, Set<String> f) {
        validPOSTags = v;
        semanticTypes = s;
        specialSemanticTypes = st;
        frequentWordsToExclude = f;
        
        NUMBER_OF_SAMPLING_STEPS = ProjectConfiguration.getNumberOfSamplingSteps();
        NUMBER_OF_EPOCHS = ProjectConfiguration.getNumberOfEpochs();
        BEAM_SIZE_TRAINING = ProjectConfiguration.getTrainingBeamSize();
        BEAM_SIZE_TEST = ProjectConfiguration.getTestBeamSize();
        
    }

    public static Model<AnnotatedDocument, State> train(List<AnnotatedDocument> trainingDocuments) {
        /*
         * Setup all necessary components for training and testing.
         */
        /*
         * Define an objective function that guides the training procedure.
         */
        ObjectiveFunction<State, String> objective = new LinkingObjectiveFunction();

        /*
         * Define templates that are responsible to generate factors/features to
         * score generated states.
         */
        List<AbstractTemplate<AnnotatedDocument, State, ?>> templates = new ArrayList<>();
//        templates.add(new ResourceTemplate(validPOSTags, semanticTypes));
//        templates.add(new PropertyTemplate(validPOSTags, semanticTypes));
        templates.add(new LexicalTemplate(validPOSTags, semanticTypes));

        /*
         * Create the scorer object that computes a score from the factors'
         * features and the templates' weight vectors.
         */
        Scorer scorer = new DefaultScorer();
        /*
         * Define a model and provide it with the necessary templates.
         */
        Model<AnnotatedDocument, State> model = new Model<>(scorer, templates);

        /*
         * Create an Initializer that is responsible for providing an initial
         * state for the sampling chain given a document.
         */
        StateInitializer initializer = new StateInitializer();

        /*
         * Define the explorers that will provide "neighboring" states given a
         * starting state. The sampler will select one of these states as a
         * successor state and, thus, perform the sampling procedure.
         */
        List<Explorer<State>> explorers = new ArrayList<>();
        explorers.add(new SingleNodeExplorer(semanticTypes, frequentWordsToExclude, validPOSTags));
        /*
         * Create a sampler that generates sampling chains with which it will
         * trigger weight updates during training.
         */

        /*
         * Stopping criterion for the sampling process. If you set this value
         * too small, the sampler can not reach the optimal solution. Large
         * values, however, increase computation time.
         */
//        StoppingCriterion<State> stoppingCriterion2 = new StepLimitCriterion<>(NUMBER_OF_SAMPLING_STEPS);
        BeamSearchStoppingCriterion<State> stoppingCriterion = new BeamSearchStoppingCriterion<State>() {

            @Override
            public boolean checkCondition(List<List<State>> chain, int step) {
                return chain.size() >= NUMBER_OF_SAMPLING_STEPS;
            }
        };

        /*
         * 
         */
        MyBeamSearchSampler<AnnotatedDocument, State, String> sampler = new MyBeamSearchSampler<>(model, objective, explorers,
                stoppingCriterion);
        sampler.setTrainSamplingStrategy(BeamSearchSamplingStrategies.greedyBeamSearchSamplingStrategyByObjective(BEAM_SIZE_TRAINING, s -> s.getObjectiveScore()));
        sampler.setTrainAcceptStrategy(AcceptStrategies.strictObjectiveAccept());

//        MySampler<AnnotatedDocument, State, String> sampler = new MySampler<>(model, objective, explorers,
//                stoppingCriterion);
//        sampler.setTrainingSamplingStrategy(SamplingStrategies.greedyObjectiveStrategy());
//        sampler.setTrainingAcceptStrategy(AcceptStrategies.objectiveAccept());
        /*
         * Define a learning strategy. The learner will receive state pairs
         * which can be used to update the models parameters.
         */
        Learner<State> learner = new AdvancedLearner<>(model, new SGD());

        log.info("####################");
        log.info("Start training");

        /*
         * The trainer will loop over the data and invoke sampling and learning.
         * Additionally, it can invoke predictions on new data.
         */
        NELTrainer trainer = new NELTrainer();
        //hybrid training procedure, switches every epoch to another scoring method {objective or model}
        trainer.addEpochCallback(new NELHybridSamplingStrategyCallback(sampler, BEAM_SIZE_TRAINING));

        //train the model
        List<SampledMultipleInstance<AnnotatedDocument, String, State>> trainResults = trainer.train(sampler, initializer, learner, trainingDocuments, i -> i.getGoldQueryString(), NUMBER_OF_EPOCHS);

        System.out.println(model.toDetailedString());
        
        //log the parsing coverage
        
        Performance.logTrain();
        
        
        return model;
    }

    public static void test(Model<AnnotatedDocument, State> model, List<AnnotatedDocument> testDocuments) {
        /*
         * Setup all necessary components for training and testing.
         */
        /*
         * Define an objective function that guides the training procedure.
         */
        ObjectiveFunction<State, String> objective = new LinkingObjectiveFunction();

        /*
         * Define templates that are responsible to generate factors/features to
         * score generated states.
         */
        List<AbstractTemplate<AnnotatedDocument, State, ?>> templates = new ArrayList<>();
//        templates.add(new ResourceTemplate(validPOSTags, semanticTypes));
//        templates.add(new PropertyTemplate(validPOSTags, semanticTypes));
        templates.add(new LexicalTemplate(validPOSTags, semanticTypes));

        /*
         * initialize QATemplateFactory
         */
        QATemplateFactory.initialize(validPOSTags, semanticTypes);

        /*
         * Create an Initializer that is responsible for providing an initial
         * state for the sampling chain given a document.
         */
        StateInitializer initializer = new StateInitializer();


        /*
         * Define the explorers that will provide "neighboring" states given a
         * starting state. The sampler will select one of these states as a
         * successor state and, thus, perform the sampling procedure.
         */
        List<Explorer<State>> explorers = new ArrayList<>();
        explorers.add(new SingleNodeExplorer(semanticTypes, frequentWordsToExclude, validPOSTags));
        /*
         * Create a sampler that generates sampling chains with which it will
         * trigger weight updates during training.
         */

        /*
         * Stopping criterion for the sampling process. If you set this value
         * too small, the sampler can not reach the optimal solution. Large
         * values, however, increase computation time.
         */
        BeamSearchStoppingCriterion<State> stoppingCriterion = new BeamSearchStoppingCriterion<State>() {

            @Override
            public boolean checkCondition(List<List<State>> chain, int step) {
                return chain.size() >= NUMBER_OF_SAMPLING_STEPS;
            }
        };

        /*
         * 
         */
        MyBeamSearchSampler<AnnotatedDocument, State, String> sampler = new MyBeamSearchSampler<>(model, objective, explorers,
                stoppingCriterion);
        sampler.setTestSamplingStrategy(BeamSearchSamplingStrategies.greedyBeamSearchSamplingStrategyByModel(BEAM_SIZE_TEST, s -> s.getModelScore()));
        sampler.setTestAcceptStrategy(AcceptStrategies.strictModelAccept());

        log.info("####################");
        log.info("Start testing");

        /*
         * The trainer will loop over the data and invoke sampling.
         */
        NELTrainer trainer = new NELTrainer();

        List<SampledMultipleInstance<AnnotatedDocument, String, State>> testResults = trainer.test(sampler, initializer, testDocuments, i -> i.getGoldQueryString());
        /*
         * Since the test function does not compute the objective score of its
         * predictions, we do that here, manually, before we print the results.
         */

        Performance.logTest(testResults, objective);
        
//        double overAllScore = 0;
//        String testPrint = "HERE IS THE START OF PRINT";
//        int c = 0;
//        for (SampledMultipleInstance<AnnotatedDocument, String, State> triple : testResults) {
//
//            double maxScore = 0;
//            State maxState = null;
//            for (State state : triple.getStates()) {
//                double s = objective.score(state, triple.getGoldResult());
//
//                if (maxState == null) {
//                    maxState = state;
//                }
//
//                if (s > maxScore) {
//                    maxScore = s;
//                    maxState = state;
//                }
//
//            }
//
////            testPrint += maxState + "\nScore: " + maxScore + "\n========================================================================\n";
//
//            overAllScore += maxScore;
//            
//            if(maxScore == 1.0){
//                c ++;
//            }
//            else{
//                testPrint += maxState + "\nScore: " + maxScore + "\n========================================================================\n";
//            }
//        }
//
//        double MACROF1 = overAllScore / (double) testResults.size();
//
//        testPrint += "\nTest results : " + MACROF1 + " Instances with 1.0: "+ c;
//
//        System.out.println(testPrint);

//        log.info(testPrint);
        /*
         * Now, that the predicted states have there objective score computed
         * and set to their internal variable, we can print the prediction
         * outcome.
         */
//        log.info("Test results:");
//        EvaluationUtil
//                .printPredictionPerformance(testResults.stream().map(t -> t.getState()).collect(Collectors.toList()));
//        /*
//         * Finally, print the models weights.
//         */
//        log.debug("Model weights:");
//        EvaluationUtil.printWeights(model, -1);
//
//        //evaluator
//        Map<String, Double> scores = ResultEvaluator.evaluateAllByObjective(testResults, objective);
//        System.out.println("Evaluation : \t" + scores);
    }
    
    public static void test(String pathToModel, List<AnnotatedDocument> testDocuments){
        QATemplateFactory.initialize(validPOSTags, semanticTypes);
        
        List<AbstractTemplate<AnnotatedDocument, State, ?>> templates = new ArrayList<>();
//        templates.add(new ResourceTemplate(validPOSTags, semanticTypes));
//        templates.add(new PropertyTemplate(validPOSTags, semanticTypes));
        templates.add(new LexicalTemplate(validPOSTags, semanticTypes));

        /*
         * Create the scorer object that computes a score from the factors'
         * features and the templates' weight vectors.
         */
        Scorer scorer = new DefaultScorer();
        /*
         * Define a model and provide it with the necessary templates.
         */
        Model<AnnotatedDocument, State> model = new Model<>(scorer, templates);
        
        /*
         * initialize QATemplateFactory
         */
        QATemplateFactory.initialize(validPOSTags, semanticTypes);
        
        QATemplateFactory f = new QATemplateFactory();
        
        try {
            model.loadModelFromDir(pathToModel, f);
            
            test(model, testDocuments);
            
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnkownTemplateRequestedException ex) {
            java.util.logging.Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Pipeline.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
