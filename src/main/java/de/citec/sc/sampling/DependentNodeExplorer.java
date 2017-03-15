/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.sampling;

import de.citec.sc.query.Candidate;
import de.citec.sc.query.Instance;
import de.citec.sc.query.ManualLexicon;

import de.citec.sc.query.Search;
import de.citec.sc.utils.DBpediaEndpoint;
import de.citec.sc.utils.Stopwords;
import de.citec.sc.variable.State;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sampling.Explorer;

/**
 *
 * @author sherzod
 */
public class DependentNodeExplorer implements Explorer<State> {

    private Map<Integer, String> semanticTypes;
    private Set<String> validPOSTags;
    private Set<String> frequentWordsToExclude;

    public DependentNodeExplorer(Map<Integer, String> assignedDUDES, Set<String> validPOSTags, Set<String> wordToExclude) {
        this.semanticTypes = assignedDUDES;
        this.validPOSTags = validPOSTags;
        this.frequentWordsToExclude = wordToExclude;
    }

    @Override
    public List getNextStates(State currentState) {
        List<State> newStates = new ArrayList<>();

        for (int indexOfNode : currentState.getDocument().getParse().getNodes().keySet()) {
            String node = currentState.getDocument().getParse().getNodes().get(indexOfNode);

            String pos = currentState.getDocument().getParse().getPOSTag(indexOfNode);

            if (!validPOSTags.contains(pos)) {
                continue;
            }
            if (frequentWordsToExclude.contains(node.toLowerCase())) {
                continue;
            }
            //assign all dudes
            for (Integer indexOfDude : semanticTypes.keySet()) {

                String dudeName = semanticTypes.get(indexOfDude);

//                boolean hasUppercase = !node.equals(node.toLowerCase());
                //Australian, Swedish, Danish => restriction classes
//                    if (hasUppercase && pos.startsWith("JJ")) {
//                        //add restriction, it can only be restriction class
//                        if (!dudeName.equals("RestrictionClass")) {
//                            continue;
//                        }
//                    }
                
                Set<Candidate> headNodeCandidates = getDBpediaMatches(dudeName, node);

                if (headNodeCandidates.isEmpty()) {
                    continue;
                }

                List<Integer> depNodes = currentState.getDocument().getParse().getDependentEdges(indexOfNode, validPOSTags, frequentWordsToExclude);
                /**
                 * if dep nodes is empty get sibling nodes
                 */
                if (depNodes.isEmpty()) {
                    int headOfHeadNodeIndex = currentState.getDocument().getParse().getParentNode(indexOfNode);
                    String headOfHeadPOS = currentState.getDocument().getParse().getPOSTag(headOfHeadNodeIndex);
                    String headOfHeadToken = currentState.getDocument().getParse().getToken(headOfHeadNodeIndex);
                    
                    if(frequentWordsToExclude.contains(headOfHeadToken)){
                        depNodes = currentState.getDocument().getParse().getSiblings(indexOfNode, validPOSTags, frequentWordsToExclude);
                    }
                }

                for (Integer depNodeIndex : depNodes) {
                    
                    //greedy exploring, skip nodes with assigned URI
                    if(!currentState.getHiddenVariables().get(depNodeIndex).getCandidate().getUri().equals("EMPTY_STRING")){
                        continue;
                    }
                    
                    String depNode = currentState.getDocument().getParse().getNodes().get(depNodeIndex);


                    for (Integer indexOfDepDude : semanticTypes.keySet()) {

                        String depDudeName = semanticTypes.get(indexOfDepDude);

                        Set<Candidate> depNodeCandidates = getDBpediaMatches(depDudeName, depNode);

                        if (depNodeCandidates.isEmpty()) {
                            continue;
                        }

                        for (Candidate headNodeCandidate : headNodeCandidates) {
                            for (Candidate depNodeCandidate : depNodeCandidates) {

                                if(headNodeCandidate.getUri().equals("http://dbpedia.org/ontology/field###http://dbpedia.org/resource/Oceanography") && depNodeCandidate.getUri().equals("http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/Sweden")){
                                    int z=1;
                                }
                                boolean isSubject = DBpediaEndpoint.isSubjectTriple(headNodeCandidate.getUri(), depNodeCandidate.getUri());
                                boolean isObject = DBpediaEndpoint.isObjectTriple(headNodeCandidate.getUri(), depNodeCandidate.getUri());

                                if (isSubject) {
                                    State s = new State(currentState);

                                    s.addHiddenVariable(indexOfNode, indexOfDude, headNodeCandidate);
                                    s.addHiddenVariable(depNodeIndex, indexOfDepDude, depNodeCandidate);
                                    
                                    //Argument is 1 => subj
                                    s.addSlotVariable(depNodeIndex, indexOfNode, 1);

                                    if (!s.equals(currentState)) {
                                        newStates.add(s);
                                    }
                                }
                                if (isObject) {
                                    State s = new State(currentState);

                                    s.addHiddenVariable(indexOfNode, indexOfDude, headNodeCandidate);
                                    s.addHiddenVariable(depNodeIndex, indexOfDepDude, depNodeCandidate);

                                    //Argument number is 2 => obj
                                    s.addSlotVariable(depNodeIndex, indexOfNode, 2);
                                    
                                    if (!s.equals(currentState)) {
                                        newStates.add(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return newStates;
    }

    private Set<Candidate> getDBpediaMatches(String dude, String node) {
        //predicate DUDE
        Set<Candidate> uris = new LinkedHashSet<>();

        //check if given node is of datatype
        //if it's datatype return the node text as URI for the assigned dude
        if (isTokenDataType(node)) {
            Candidate i = new Candidate(null, 0, 0, 0);
            uris.add(i);

            return uris;
        }

        boolean useLemmatizer = false;
        boolean useWordNet = false;
        boolean mergePartialMatches = false;

        int topK = 100;

        String queryTerm = node.toLowerCase().trim();

        switch (dude) {
            case "Property":
                useLemmatizer = true;
                useWordNet = false;
                mergePartialMatches = false;

                if (!Stopwords.isStopWord(queryTerm)) {
                    Set<Candidate> propertyURIs = Search.getPredicates(queryTerm, topK, useLemmatizer, mergePartialMatches, useWordNet);
                    uris.addAll(propertyURIs);
                }

                //retrieve manual lexicon even if it's in stop word list
                if (ManualLexicon.useManualLexicon) {
                    Set<String> definedLexica = ManualLexicon.getProperties(queryTerm);
                    for (String d : definedLexica) {
                        uris.add(new Candidate(new Instance(d, 10000), 0, 1.0, 1.0));
                    }
                }
                break;

            case "Class":
                useLemmatizer = true;
                mergePartialMatches = false;
                useWordNet = false;
                if (!Stopwords.isStopWord(queryTerm)) {
                    Set<Candidate> classURIs = Search.getClasses(queryTerm, topK, useLemmatizer, mergePartialMatches, useWordNet);

                    uris.addAll(classURIs);
                }

                //retrieve manual lexicon even if it's in stop word list
                if (ManualLexicon.useManualLexicon) {
                    Set<String> definedLexica = ManualLexicon.getClasses(queryTerm);
                    for (String d : definedLexica) {
                        uris.add(new Candidate(new Instance(d, 10000), 0, 1.0, 1.0));
                    }
                }
                break;
            case "RestrictionClass":
                useLemmatizer = true;
                useWordNet = false;
                mergePartialMatches = false;

                if (!Stopwords.isStopWord(queryTerm)) {
                    Set<Candidate> restrictionClassURIs = Search.getRestrictionClasses(queryTerm, topK, useLemmatizer, mergePartialMatches, useWordNet);
                    uris.addAll(restrictionClassURIs);
                }

                //check manual lexicon for Restriction Classes
                if (ManualLexicon.useManualLexicon) {
                    Set<String> definedLexica = ManualLexicon.getRestrictionClasses(queryTerm);
                    for (String d : definedLexica) {
                        uris.add(new Candidate(new Instance(d, 10000), 0, 1.0, 1.0));
                    }
                }
                break;
            case "UnderSpecifiedClass":
                topK = 10;
                useLemmatizer = false;
                mergePartialMatches = false;
                useWordNet = false;

                //extract resources
                if (!Stopwords.isStopWord(queryTerm)) {
                    Set<Candidate> resourceURIs = Search.getResources(queryTerm, topK, useLemmatizer, mergePartialMatches, useWordNet);

                    //set some empty propertyy
                    for (Candidate c : resourceURIs) {
                        c.setUri("<someProperty>###" + c.getUri());
                    }

                    uris.addAll(resourceURIs);
                }

                //check manual lexicon for Resources => to make underspecified class
                if (ManualLexicon.useManualLexicon) {
                    Set<String> definedLexica = ManualLexicon.getResources(queryTerm);
                    for (String d : definedLexica) {
                        uris.add(new Candidate(new Instance("<someProperty>###" + d, 10000), 0, 1.0, 1.0));
                    }
                }
                break;
            case "Individual":
                topK = 10;
                useLemmatizer = false;
                mergePartialMatches = false;
                useWordNet = false;
                if (!Stopwords.isStopWord(queryTerm)) {
                    Set<Candidate> resourceURIs = Search.getResources(queryTerm, topK, useLemmatizer, mergePartialMatches, useWordNet);
                    uris.addAll(resourceURIs);
                }

                //check manual lexicon
                if (ManualLexicon.useManualLexicon) {
                    Set<String> definedLexica = ManualLexicon.getResources(queryTerm);
                    for (String d : definedLexica) {
                        uris.add(new Candidate(new Instance(d, 10000), 0, 1.0, 1.0));
                    }
                }
                break;
        }

        return uris;
    }

    /**
     * checks if given token is type of data
     *
     * @return true if it's data type
     */
    private boolean isTokenDataType(String tokens) {
        List<String> patterns = new ArrayList<>();

        String monthYearPattern = "((January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec)\\s+((19|20)\\d\\d))";//March 2015, Aug 1999
        monthYearPattern += "|((0?[1-9]|1[012])/((19|20)\\d\\d))"; //mm/YYYY
        monthYearPattern += "|((0?[1-9]|1[012])-((19|20)\\d\\d))"; //mm-YYYY

        String datePattern = "((0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d))";//dd/MM/YYYY
        datePattern += "|((0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d))"; //dd-MM-YYYY
        datePattern += "|((January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec)\\s+((1st)|(2nd)|(3rd)|(([1-9]|[12][0-9]|3[01])(th)))(,)?\\s+((19|20)\\d\\d))"; //Jan 1st 2015, August 14th 1999
        datePattern += "|((January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec)\\s+(0?[1-9]|[12][0-9]|3[01])(,)?\\s+((19|20)\\d\\d))"; //Jan 1st 2015, August 14th 1999
        datePattern += "|((0?[1-9]|[12][0-9]|3[01])\\s+(January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec)\\s+((19|20)\\d\\d))"; //Jan 1st 2015, August 14th 1999
        datePattern += "|((0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d))"; //MM/dd/YYYY
        datePattern += "|((0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])-((19|20)\\d\\d))"; //MM-dd-YYYY

        patterns.add(monthYearPattern);
        patterns.add(datePattern);
        patterns.add("^(http://|https://|www.)+.+$");
        patterns.add("^(true|false)+.*$");
        patterns.add("(^[-+]?\\d+)");
        patterns.add("(^[+]?\\d+)");
        patterns.add("(^[+]?\\d+)");
        patterns.add("(^[-+]?\\d+\\.\\d+)");
        patterns.add("(^[-+]?\\d+\\.\\d+)");
        patterns.add("\\d{4}");

        for (String pattern : patterns) {
            if (tokens.matches(pattern)) {
                return true;
            }
        }

        return false;
    }
}
