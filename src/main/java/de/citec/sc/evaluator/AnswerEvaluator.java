/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.evaluator;

import de.citec.sc.qald.Predicate;
import de.citec.sc.qald.SPARQLParser;
import de.citec.sc.qald.Term;
import de.citec.sc.qald.Triple;
import de.citec.sc.qald.Variable;
import de.citec.sc.utils.DBpediaEndpoint;
import de.citec.sc.variable.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class AnswerEvaluator  {

    
    public static double evaluate(String derived, String goldStandard) {

        //if the body doesn't have any triples the similarity is zero
        if (SPARQLParser.extractTriplesFromQuery(derived).isEmpty()) {
            return 0;
        }

        List<String> candidateAnswers = DBpediaEndpoint.runQuery(derived);

        List<String> goldAnswers = DBpediaEndpoint.runQuery(goldStandard);

        double f1 = getF1(candidateAnswers, goldAnswers);

        return f1;
    }

    private static double getF1(List<String> a, List<String> b) {

        int r = 0;

        for (String s : a) {
            if (b.contains(s)) {
                r++;
            }
        }

//        if(a.isEmpty() && b.isEmpty()){
//            return 1.0;
//            
//        }
        double recall = (double) r / (double) b.size();
        double precision = (double) r / (double) a.size();

        double f1 = (2 * precision * recall) / (precision + recall);

        if (Double.isNaN(f1)) {

            f1 = 0;
        }

        return f1;

    }
}
