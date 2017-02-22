package de.citec.sc.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import corpus.SampledInstance;
import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.evaluator.ResultEvaluator;
import de.citec.sc.learning.LinkingObjectiveFunction;
import de.citec.sc.query.Search;
import de.citec.sc.sampling.MySampler;
import de.citec.sc.sampling.SingleNodeExplorer;
import de.citec.sc.sampling.StateInitializer;
import de.citec.sc.template.NodeSimilarityTemplate;
import de.citec.sc.variable.State;
import evaluation.EvaluationUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import learning.AdvancedLearner;
import learning.Learner;
import learning.Model;
import learning.ObjectiveFunction;
import learning.Trainer;
import learning.optimizer.SGD;
import learning.scorer.DefaultScorer;
import learning.scorer.Scorer;
import sampling.DefaultSampler;
import sampling.Explorer;
import sampling.samplingstrategies.AcceptStrategies;
import sampling.samplingstrategies.SamplingStrategies;
import sampling.stoppingcriterion.StepLimitCriterion;
import sampling.stoppingcriterion.StoppingCriterion;
import templates.AbstractTemplate;

/**
 *
 * @author sjebbara
 */
public class Pipeline {

    private static final int NUMBER_OF_SAMPLING_STEPS = 10;
    private static final int NUMBER_OF_EPOCHS = 1;
    private static final int MAX_NUMBER_OF_CANDIDATES = 10;
    private static Logger log = LogManager.getFormatterLogger();

    public static Model<AnnotatedDocument, State> train(List<AnnotatedDocument> trainingDocuments, Search indexLookUp, Map<Integer, String> semanticTypes) {
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
        templates.add(new NodeSimilarityTemplate());

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
        explorers.add(new SingleNodeExplorer(indexLookUp, semanticTypes));
        /*
         * Create a sampler that generates sampling chains with which it will
         * trigger weight updates during training.
         */

        /*
         * Stopping criterion for the sampling process. If you set this value
         * too small, the sampler can not reach the optimal solution. Large
         * values, however, increase computation time.
         */
        StoppingCriterion<State> stoppingCriterion = new StepLimitCriterion<>(NUMBER_OF_SAMPLING_STEPS);

        /*
         * 
         */
        MySampler<AnnotatedDocument, State, String> sampler = new MySampler<>(model, objective, explorers,
                stoppingCriterion);
        sampler.setTrainingSamplingStrategy(SamplingStrategies.greedyObjectiveStrategy());
        sampler.setTrainingAcceptStrategy(AcceptStrategies.objectiveAccept());
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
        Trainer trainer = new Trainer();
        trainer.train(sampler, initializer, learner, trainingDocuments, NUMBER_OF_EPOCHS);

        System.out.println(model.toDetailedString());
        return model;
    }

    public static void test(Model<AnnotatedDocument, State> model, List<AnnotatedDocument> testDocuments, Search indexLookUp, Map<Integer, String> semanticTypes) {
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
        templates.add(new NodeSimilarityTemplate());

        /*
         * Create an Initializer that is responsible for providing an initial
         * state for the sampling chain given a document.
         */
        StateInitializer initializer = new StateInitializer();
        
        HashMap<Integer, String> assignedDUDES = new LinkedHashMap<>();
        
        assignedDUDES.put(1, "Property");
        assignedDUDES.put(2, "Individual");
        assignedDUDES.put(3, "Class");
        assignedDUDES.put(4, "UnderSpecifiedClass");
        
        /*
         * Define the explorers that will provide "neighboring" states given a
         * starting state. The sampler will select one of these states as a
         * successor state and, thus, perform the sampling procedure.
         */
        List<Explorer<State>> explorers = new ArrayList<>();
        explorers.add(new SingleNodeExplorer(indexLookUp, assignedDUDES));
        /*
         * Create a sampler that generates sampling chains with which it will
         * trigger weight updates during training.
         */

        /*
         * Stopping criterion for the sampling process. If you set this value
         * too small, the sampler can not reach the optimal solution. Large
         * values, however, increase computation time.
         */
        StoppingCriterion<State> stoppingCriterion = new StepLimitCriterion<>(NUMBER_OF_SAMPLING_STEPS);

        /*
         * 
         */
        DefaultSampler<AnnotatedDocument, State, String> sampler = new DefaultSampler<>(model, objective, explorers,
                stoppingCriterion);

        log.info("####################");
        log.info("Start testing");

        /*
         * The trainer will loop over the data and invoke sampling.
         */
        Trainer trainer = new Trainer();
        List<SampledInstance<AnnotatedDocument, String, State>> testResults = trainer.test(sampler,
                initializer, testDocuments);
        /*
         * Since the test function does not compute the objective score of its
         * predictions, we do that here, manually, before we print the results.
         */

        for (SampledInstance<AnnotatedDocument, String, State> triple : testResults) {
            double s = objective.score(triple.getState(), triple.getGoldResult());
        }
        /*
         * Now, that the predicted states have there objective score computed
         * and set to their internal variable, we can print the prediction
         * outcome.
         */
        log.info("Test results:");
        EvaluationUtil
                .printPredictionPerformance(testResults.stream().map(t -> t.getState()).collect(Collectors.toList()));
        /*
         * Finally, print the models weights.
         */
        log.debug("Model weights:");
        EvaluationUtil.printWeights(model, -1);



        //evaluator
        Map<String, Double> scores = ResultEvaluator.evaluateAllByObjective(testResults, objective);
        System.out.println("Evaluation : \t" + scores);

    }
}
