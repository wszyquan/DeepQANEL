package de.citec.sc.main;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.corpus.QALDCorpus;
import de.citec.sc.qald.QALDCorpusLoader;
import de.citec.sc.query.CandidateRetriever;
import de.citec.sc.query.CandidateRetrieverOnLucene;
import de.citec.sc.query.ManualLexicon;
import de.citec.sc.query.Search;
import de.citec.sc.utils.DBpediaEndpoint;
import de.citec.sc.wordNet.WordNetAnalyzer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import learning.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getFormatterLogger();

    public static void main(String[] args) {

        CandidateRetriever retriever = new CandidateRetrieverOnLucene(true, "luceneIndexes/resourceIndex", "luceneIndexes/classIndex", "luceneIndexes/predicateIndex", "luceneIndexes/matollIndex");

        WordNetAnalyzer wordNet = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");

        Search indexLookUp = new Search(retriever, wordNet);
        Search.useMatoll(true);
        ManualLexicon.useManualLexicon(true);
        
        Map<Integer, String> semanticTypes = new LinkedHashMap<>();

        semanticTypes.put(1, "Property");
        semanticTypes.put(2, "Individual");
        semanticTypes.put(3, "Class");
        semanticTypes.put(4, "UnderSpecifiedClass");
        
        Set<String> validPOSTags = new HashSet<>();
        validPOSTags.add("NN");
        validPOSTags.add("NNP");
        validPOSTags.add("NNS");
        validPOSTags.add("VBZ");
        validPOSTags.add("VBD");
        validPOSTags.add("JJ");


        QALDCorpus trainCorpus = QALDCorpusLoader.load(QALDCorpusLoader.Dataset.qald6Train);
        QALDCorpus testCorpus = QALDCorpusLoader.load(QALDCorpusLoader.Dataset.qald6Test);

        List<AnnotatedDocument> trainDocuments = getDocuments(trainCorpus, indexLookUp);
        List<AnnotatedDocument> testDocuments = getDocuments(testCorpus, indexLookUp);
        
        Model trainedModel = Pipeline.train(trainDocuments, indexLookUp, semanticTypes);
        
        Pipeline.test(trainedModel, testDocuments, indexLookUp, semanticTypes);

    }

    private static List<AnnotatedDocument> getDocuments(QALDCorpus corpus, Search indexLookUp) {
        List<AnnotatedDocument> documents = new ArrayList<>();

        for (AnnotatedDocument d1 : corpus.getDocuments()) {
            String question = d1.getQuestionString();

            if (d1.getQaldInstance().getAggregation().equals("false") && d1.getQaldInstance().getOnlyDBO().equals("true") && d1.getQaldInstance().getHybrid().equals("false")) {

                if (DBpediaEndpoint.isValidQuery(d1.getGoldQueryString(), false)) {

                    d1.getParse().mergeEdges(indexLookUp);
                    documents.add(d1);
                }
            }
        }

        return documents;
    }
}
