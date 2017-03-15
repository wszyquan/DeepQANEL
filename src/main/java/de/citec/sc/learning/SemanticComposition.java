/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.learning;

import de.citec.sc.dudes.rdf.RDFDUDES;
import de.citec.sc.variable.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class SemanticComposition {
    
    private static Map<Integer, RDFDUDES> dudeTypes;
    private static Set<String> validPOSTags;
    private static Set<String> frequentWordsToExclude;
    
    protected static RDFDUDES compose(State state, Map<Integer, RDFDUDES> instantiatedDudeTypes, Set<String> validPOSs, Set<String> f) {
        Integer headNode = state.getDocument().getParse().getHeadNode();
        
        validPOSTags = validPOSs;
        dudeTypes = instantiatedDudeTypes;
        frequentWordsToExclude = f;
        
        RDFDUDES merged = mergedChildNodes(state, headNode);
        
        return merged;
    }
    
    private static RDFDUDES mergedChildNodes(State state, Integer node) {
        //get dependent nodes
        List<Integer> dependentNodes = state.getDocument().getParse().getDependentEdges(node, validPOSTags, frequentWordsToExclude);

        //get siblins if there are not dependent nodes
        if (dependentNodes.isEmpty()) {
            if (frequentWordsToExclude.contains(state.getDocument().getParse().getToken(node))) {
                dependentNodes = state.getDocument().getParse().getSiblings(node, validPOSTags, frequentWordsToExclude);
            }
            
        }
        
        for (Integer depNodeIndex : dependentNodes) {
            
            RDFDUDES dependent = mergedChildNodes(state, depNodeIndex);
            RDFDUDES head = dudeTypes.get(node);
            
            String argument = state.getSlotVariables().get(depNodeIndex).getSlotNumber() + "";

//                try {
//                    System.out.println("HEAD " + head + " Node: "+node);
//                    System.out.println("ARGUMENT " + argument);
//
//                    System.out.println("DEP: " + dependent + " Node : "+child);
//
//                } catch (Exception e) {
//
//                }
//                
            head = merge(head, dependent, argument);
            dudeTypes.put(node, head);
        }
        return dudeTypes.get(node);
    }
    
    private static RDFDUDES merge(RDFDUDES head, RDFDUDES dependent, String argument) {
        if (head == null) {
            return dependent;
        } else if (dependent == null || argument.equals("-1")) {
            return head;
        } else {
            try {
                
                head = head.merge(dependent, argument);
            } catch (Exception e) {
                
            }
        }
        return head;
    }
}
