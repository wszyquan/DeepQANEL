/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fullStack;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.evaluator.QueryEvaluator;
import de.citec.sc.learning.QueryConstructor;
import de.citec.sc.parser.DependencyParse;
import de.citec.sc.qald.Question;
import de.citec.sc.query.Candidate;
import de.citec.sc.variable.State;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author sherzod
 */
public class QueryConstructorTest {
   
    
//    public static void main(String[] args){
    @Test
    public void test(){
        
        //semantic types to sample from
        Map<Integer, String> semanticTypes = new LinkedHashMap<>();
        semanticTypes.put(1, "Property");
        semanticTypes.put(2, "Individual");
        semanticTypes.put(3, "Class");
        semanticTypes.put(4, "RestrictionClass");
        
       
        //semantic types with special meaning
        Map<Integer, String> specialSemanticTypes = new LinkedHashMap<>();
        specialSemanticTypes.put(5, "What");//it should be higher than semantic type size
        
        
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
        frequentWordsToExclude.add("belong");
        
        QueryConstructor.initialize(specialSemanticTypes, semanticTypes, validPOSTags, frequentWordsToExclude);
        
        
        /**
         * Text: Who created Family_Guy
         * Nodes : 1    2       3
         * Edges: (1,2 = subj)  (3,2 = dobj)
         */
        DependencyParse parseTree = new DependencyParse();
        parseTree.addNode(1, "Who", "WDT", -1, -1);
        parseTree.addNode(2, "created", "VBP", -1, -1);
        parseTree.addNode(3, "Family_Guy", "NNP", -1, -1);
        
        parseTree.addEdge(1, 2, "subj");
        parseTree.addEdge(3, 2, "obj");
        
        parseTree.setHeadNode(2);
        
        
        Question qaldInstance = new Question("Who created Family_Guy", "SELECT DISTINCT ?uri WHERE { <http://dbpedia.org/resource/Family_Guy> <http://dbpedia.org/ontology/creator> ?uri . }  ");
        qaldInstance.setId("1");
        
        
        AnnotatedDocument doc = new AnnotatedDocument(parseTree, qaldInstance);
        
        State state = new State(doc);
        
        Candidate c3 = new Candidate("http://dbpedia.org/resource/Family_Guy", 0, 0, 0, null, null, null, null, null);
        Candidate c2 = new Candidate("http://dbpedia.org/ontology/creator", 0, 0, 0, null, null, null, null, null);
        Candidate c1 = new Candidate("EMPTY_STRING", 0, 0, 0, null, null, null, null, null);
        
        state.addHiddenVariable(1, 5, c1);
        state.addHiddenVariable(2, 1, c2);
        state.addHiddenVariable(3, 2, c3);
        
        state.addSlotVariable(3, 2, 1);
        state.addSlotVariable(1, 2, 2);
        
        System.out.println(state.toString());
        
        String query = QueryConstructor.getSPARQLQuery(state);
        
        System.out.println("Constructed Query : \n"+query);
        
        String expectedQuery = "SELECT DISTINCT ?uri WHERE { <http://dbpedia.org/resource/Family_Guy> <http://dbpedia.org/ontology/creator> ?uri . }  ";
        
        double simScore = QueryEvaluator.evaluate(query, expectedQuery);
        
        System.out.println("Similarity score to expected query: " + simScore);
        
        Assert.assertEquals(1.0, simScore);
        
        
    }
}
