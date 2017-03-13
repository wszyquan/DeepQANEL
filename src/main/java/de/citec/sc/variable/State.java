/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.variable;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.query.Candidate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import utility.StateID;
import variables.AbstractState;

/**
 *
 * @author sherzod
 */
public class State extends AbstractState<AnnotatedDocument> {

    private AnnotatedDocument document;

    private Map<Integer, HiddenVariable> hiddenVariables;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.document);
        hash = 17 * hash + Objects.hashCode(this.hiddenVariables);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State other = (State) obj;
        if (!Objects.equals(this.document, other.document)) {
            return false;
        }
        if (!Objects.equals(this.hiddenVariables, other.hiddenVariables)) {
            return false;
        }
        return true;
    }

    public Map<Integer, HiddenVariable> getHiddenVariables() {
        return hiddenVariables;
    }

    public void setHiddenVariables(Map<Integer, HiddenVariable> hiddenVariables) {
        this.hiddenVariables = hiddenVariables;
    }


    public State(AnnotatedDocument instance) {
        super(instance);
        this.document = (AnnotatedDocument) document;
        this.hiddenVariables = new TreeMap<>();

    }

    public State(State state) {
        super(state);

        this.setDocument(state.document);

        //clone dudes
        HashMap<Integer, HiddenVariable> hiddenVariables = new HashMap<>();
        for (Integer d : state.hiddenVariables.keySet()) {
            hiddenVariables.put(d, state.hiddenVariables.get(d).clone());
        }

        this.hiddenVariables = hiddenVariables;
    }
    
    


    public void addHiddenVariable(Integer indexOfNode, Integer indexOfDUDE, Candidate c) {
        
        HiddenVariable v = new HiddenVariable(indexOfNode, indexOfDUDE, c);
        this.hiddenVariables.put(indexOfNode, v);

    }



    @Override
    public String toString() {
        String state = "Document: " + "\n" + document.toString() + "\n";

        state += "\nHiddenVariables:\n";
        
        for (Integer d : hiddenVariables.keySet()) {
            state += hiddenVariables.get(d).toString() + "\n";
        }

        state += "\nObjectiveScore: " + getObjectiveScore();
        state += "\nModelScore: " + getModelScore() + "\n";

        return state;
    }


    public AnnotatedDocument getDocument() {
        return document;
    }

    @Override
    public StateID getID() {
        return id;
    }

    public void setDocument(AnnotatedDocument document) {
        this.document = document;
    }



}
