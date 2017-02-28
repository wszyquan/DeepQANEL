/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.template;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.variable.HiddenVariable;

import de.citec.sc.variable.State;
import factors.Factor;
import factors.FactorScope;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import learning.Vector;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityMeasures;
import templates.AbstractTemplate;

/**
 *
 * @author sherzod
 */
public class LexicalTemplate extends AbstractTemplate<AnnotatedDocument, State, StateFactorScope<State>> {

    public LexicalTemplate() {

    }

    @Override
    public List<StateFactorScope<State>> generateFactorScopes(State state) {
        List<StateFactorScope<State>> factors = new ArrayList<>();

        for (Integer key : state.getDocument().getParse().getNodes().keySet()) {

            HiddenVariable a = state.getHiddenVariables().get(key);

            factors.add(new StateFactorScope<>(this, state));
        }

        return factors;
    }

    @Override
    public void computeFactor(Factor<StateFactorScope<State>> factor) {
        State state = factor.getFactorScope().getState();

        Vector featureVector = factor.getFeatureVector();

        //add dependency feature between tokens
        for (Integer tokenID : state.getDocument().getParse().getNodes().keySet()) {
            String headToken = state.getDocument().getParse().getToken(tokenID);
            String headURI = state.getHiddenVariables().get(tokenID).getCandidate().getUri();

            if (headURI.equals("EMPTY_STRING")) {
                continue;
            }

            List<Integer> dependentNodes = state.getDocument().getParse().getDependentEdges(tokenID);

            if (dependentNodes.isEmpty()) {
                featureVector.addToValue("LEXICAL FEATURE: URI: " + headURI + " TOKEN: " + headToken, 1.0);
            }

            if (!dependentNodes.isEmpty()) {

                for (Integer depNodeID : dependentNodes) {
                    String depToken = state.getDocument().getParse().getToken(depNodeID);
                    String depURI = state.getHiddenVariables().get(depNodeID).getCandidate().getUri();

                    if (!depURI.equals("EMPTY_STRING")) {
                        featureVector.addToValue("LEXICAL DEP FEATURE: HEAD_URI: " + headURI + " HEAD_TOKEN: " + headToken + " CHILD_URI: " + depURI + " CHILD_TOKEN: " + depToken, 1.0);
                    }
                }
            }
        }
    }

}
