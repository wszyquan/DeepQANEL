package de.citec.sc.main;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.corpus.QALDCorpus;
import de.citec.sc.qald.QALDCorpusLoader;
import de.citec.sc.query.CandidateRetriever;
import de.citec.sc.query.CandidateRetrieverOnLucene;
import de.citec.sc.query.ManualLexicon;
import de.citec.sc.query.Search;
import de.citec.sc.template.QATemplateFactory;
import de.citec.sc.utils.DBpediaEndpoint;
import de.citec.sc.utils.ProjectConfiguration;
import de.citec.sc.wordNet.WordNetAnalyzer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import learning.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getFormatterLogger();

    public static void main(String[] args) {

        if (args.length > 0) {

        } else {

            args = new String[20];
            args[0] = "-d1";//query dataset
            args[1] = "qald6Train";//qald6Train  qald6Test   qaldSubset
            args[2] = "-d2";  //test dataset
            args[3] = "qald6Train";//qald6Train  qald6Test   qaldSubset
            args[4] = "-m1";//manual lexicon
            args[5] = "true";//true, false
            args[6] = "-m2";//matoll
            args[7] = "true";//true, false
            args[8] = "-e";//epochs
            args[9] = "" + 4;
            args[10] = "-s";//sampling steps
            args[11] = "" + 3;
            args[12] = "-k";//top k samples to select from during training
            args[13] = "" + 3;
            args[14] = "-l";//top k samples to select from during testing
            args[15] = "" + 3;
            args[16] = "-w";//max word count
            args[17] = "" + 3;
            args[18] = "-t";//task name
            args[19] = "linking";//qa, linking
        }

        ProjectConfiguration.loadConfigurations(args);

        //load index, initialize postag lists etc.        
        initialize();

        //load training and testing corpus
        List<AnnotatedDocument> trainDocuments = getDocuments(QALDCorpusLoader.Dataset.valueOf(ProjectConfiguration.getTrainingDatasetName()));
        List<AnnotatedDocument> testDocuments = getDocuments(QALDCorpusLoader.Dataset.valueOf(ProjectConfiguration.getTestDatasetName()));

        //train and test model
        try {
            Model trainedModel = Pipeline.train(trainDocuments);

//            trainedModel.saveModelToFile("models", "model");
            Pipeline.test(trainedModel, testDocuments);
//            Pipeline.test("models/model", trainDocuments);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void initialize() {

        CandidateRetriever retriever = new CandidateRetrieverOnLucene(true, "luceneIndexes/resourceIndex", "luceneIndexes/classIndex", "luceneIndexes/predicateIndex", "luceneIndexes/matollIndex");

        WordNetAnalyzer wordNet = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");

        Search.load(retriever, wordNet);
        Search.useMatoll(ProjectConfiguration.useMatoll());

        ManualLexicon.useManualLexicon(ProjectConfiguration.useManualLexicon());

        Map<Integer, String> semanticTypes = new LinkedHashMap<>();

        semanticTypes.put(1, "Property");
        semanticTypes.put(2, "Individual");
        semanticTypes.put(3, "Class");
        semanticTypes.put(4, "RestrictionClass");
//        semanticTypes.put(5, "UnderSpecifiedClass");

        Map<Integer, String> specialSemanticTypes = new LinkedHashMap<>();

        specialSemanticTypes.put(1, "Who");
        specialSemanticTypes.put(2, "Which");

        Set<String> validPOSTags = new HashSet<>();
        validPOSTags.add("NN");
        validPOSTags.add("NNP");
        validPOSTags.add("NNS");
        validPOSTags.add("NNPS");
        validPOSTags.add("VBZ");
        validPOSTags.add("VBN");
        validPOSTags.add("VBD");
        validPOSTags.add("VBG");
        validPOSTags.add("VBP");
        validPOSTags.add("VB");
        validPOSTags.add("JJ");

        Set<String> frequentWordsToExclude = new HashSet<>();
        //all of this words have a valid POSTAG , so they shouldn't be assigned any URI to these tokens
        frequentWordsToExclude.add("is");
        frequentWordsToExclude.add("was");
        frequentWordsToExclude.add("were");
        frequentWordsToExclude.add("are");
        frequentWordsToExclude.add("do");
        frequentWordsToExclude.add("does");
        frequentWordsToExclude.add("did");
        frequentWordsToExclude.add("give");
        frequentWordsToExclude.add("list");
        frequentWordsToExclude.add("show");
        frequentWordsToExclude.add("me");
        frequentWordsToExclude.add("many");
        frequentWordsToExclude.add("have");

        Pipeline.initialize(validPOSTags, semanticTypes, specialSemanticTypes, frequentWordsToExclude);
    }

    private static List<AnnotatedDocument> getDocuments(QALDCorpusLoader.Dataset dataset) {

        boolean includeYAGO = false;
        boolean includeAggregation = false;
        boolean includeUNION = false;
        boolean onlyDBO = true;
        boolean isHybrid = false;

        QALDCorpus corpus = QALDCorpusLoader.load(dataset, includeYAGO, includeAggregation, includeUNION, onlyDBO, isHybrid);

        List<AnnotatedDocument> documents = new ArrayList<>();

        System.out.print("Loaded dataset : " + dataset);
        for (AnnotatedDocument d1 : corpus.getDocuments()) {
//            String question = d1.getQuestionString();

//            if (d1.getQaldInstance().getAggregation().equals("false") && d1.getQaldInstance().getOnlyDBO().equals("true") && d1.getQaldInstance().getHybrid().equals("false")) {
            if (DBpediaEndpoint.isValidQuery(d1.getGoldQueryString(), false)) {

                if (d1.getQuestionString().split(" ").length <= ProjectConfiguration.getMaxWordCount()) {
                    d1.getParse().mergeEdges();
                    documents.add(d1);
                }

            }
//            }
        }

        System.out.println(" contains " + documents.size() + " instances.");

        return documents;
    }
}
