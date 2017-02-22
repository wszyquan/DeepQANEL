/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.qald;

import de.citec.sc.corpus.QALDCorpus;
import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.parser.DependencyParse;
import de.citec.sc.parser.StanfordParser;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author sherzod
 */
public class QALDCorpusLoader {

    private static final String qald4FileTrain = "src/main/resources/qald-4_multilingual_train_withanswers.xml";
    private static final String qald4FileTest = "src/main/resources/qald-4_multilingual_test_withanswers.xml";
    private static final String qald5FileTrain = "src/main/resources/qald-5_train.xml";
    private static final String qald6FileTrain = "src/main/resources/qald-6-train-multilingual.json";
    private static final String qald6FileTest = "src/main/resources/qald-6-test-multilingual.json";
    private static final String qald5FileTest = "src/main/resources/qald-5_test.xml";
    private static final String qaldSubset = "src/main/resources/qald_test.xml";

    public enum Dataset {

        qald4Test, qald4Train, qald5Test, qald5Train, qaldSubset, qald6Train, qald6Test
    }

    /**
     * reads QALD corpus {QALDSubset, QALD4Train, QALD4Test, QALD5Train,
     * QALD5Test} and returns corpus with documents
     *
     * @param file
     * @return corpus
     */
    public static QALDCorpus load(Dataset d) {

        String filePath = "";

        List<Question> questions = new ArrayList<>();

        switch (d.name()) {
            case "qald4Train":
                filePath = qald4FileTrain;
                questions = QALD.getQuestions(filePath);
                break;
            case "qald4Test":
                filePath = qald4FileTest;
                questions = QALD.getQuestions(filePath);
                break;
            case "qald5Train":
                filePath = qald5FileTrain;
                questions = QALD.getQuestions(filePath);
                break;
            case "qald5Test":
                filePath = qald5FileTest;
                questions = QALD.getQuestions(filePath);
                break;
            case "qaldSubset":
                filePath = qaldSubset;
                questions = QALD.getQuestions(filePath);
                break;
            case "qald6Train":
                filePath = qald6FileTrain;
                questions = readJSONFile(filePath);
                break;
            case "qald6Test":
                filePath = qald6FileTest;
                questions = readJSONFile(filePath);
                break;
            default:
                System.err.println("Corpus not found!");
                System.exit(0);
        }

        QALDCorpus corpus = new QALDCorpus();
        corpus.setCorpusName(d.name());

        for (Question q : questions) {
            DependencyParse parse = StanfordParser.parse(q.getQuestionText());

            //check if there are any loops
            boolean isLoop = false;
            String loop = "";
            for (Integer k : parse.getRelations().keySet()) {
                Integer v = parse.getRelations().get(k);

                if (k.equals(v)) {
                    isLoop = true;
                    loop = k + " " + v;
                    break;
                }
                //check if any relations has v as Key, and k as Value

                if (parse.getRelations().containsKey(v)) {

                    Integer otherK = parse.getRelations().get(v);

                    if (k.equals(otherK)) {
                        isLoop = true;
                        loop = k + " " + v;
                        break;
                    }
                }
            }

            if (!isLoop) {
                
                AnnotatedDocument document = new AnnotatedDocument(parse, q);

                corpus.addDocument(document);
            } else {
                System.out.println(parse);
                System.out.println("Loop at: " + loop);

            }

        }

        return corpus;
    }

    private static List<Question> readJSONFile(String filePath) {
        ArrayList<Question> qaldQuestions = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {

            HashMap obj = (HashMap) parser.parse(new FileReader(filePath));

            JSONArray questions = (JSONArray) obj.get("questions");
            for (int i = 0; i < questions.size(); i++) {
                HashMap o1 = (HashMap) questions.get(i);

                String hybrid = (String) o1.get("hybrid");

                String answerType = (String) o1.get("answertype");

                String onlyDBO = (String) o1.get("onlydbo");

                String aggregation = (String) o1.get("aggregation");
                 String id = o1.get("id").toString();

                HashMap queryTextObj = (HashMap) o1.get("query");
                String query = (String) queryTextObj.get("sparql");
                String questionText = "";

                JSONArray questionTexts = (JSONArray) o1.get("question");
                for (Object qObject : questionTexts) {

                    JSONObject englishQuestionText = (JSONObject) qObject;

                    if (englishQuestionText.get("language").equals("en")) {
                        questionText = (String) englishQuestionText.get("string");
                        break;
                    }
                }

                if (query != null) {
                    if (!query.equals("")) {

                        if (query.contains("UNION")) {
                            query = removeUNION(query);
                        }
                        Question q1 = new Question(questionText, query, onlyDBO, aggregation, answerType, hybrid, id);
                        qaldQuestions.add(q1);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return qaldQuestions;
    }

    private static String removeUNION(String q) {

        while (q.contains("UNION")) {
            String s1 = q.substring(q.indexOf("UNION"));
            String s2 = s1.substring(0, s1.indexOf("}") + 1);

            q = q.replace(s2, "");
            String tail = q.substring(q.lastIndexOf("}") + 1);
            q = q.replace(tail, " ");
            q = q.replace("{", " ");
            q = q.replace("}", " ");
            q = q.replace("WHERE", "WHERE { ");
            q = q + "}" + tail;
        }

        return q;
    }
}
