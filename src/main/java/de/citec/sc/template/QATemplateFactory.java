/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.template;

import de.citec.sc.corpus.AnnotatedDocument;
import de.citec.sc.variable.State;
import exceptions.UnkownTemplateRequestedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import templates.AbstractTemplate;
import templates.TemplateFactory;

/**
 *
 * @author sherzod
 */
public class QATemplateFactory implements TemplateFactory<AnnotatedDocument, State> {

    private static Set<String> validPOSTags;
    private static Set<String> frequentWordsToExclude;
    private static Map<Integer, String> semanticTypes;

    public static void initialize(Set<String> v, Set<String> f, Map<Integer, String> s) {
        validPOSTags = v;
        semanticTypes = s;
        frequentWordsToExclude = f;
    }

    @Override
    public AbstractTemplate<AnnotatedDocument, State, ?> newInstance(String templateName) throws UnkownTemplateRequestedException, Exception {

        switch (templateName) {
            case "NodeSimilarityTemplate":
                return new NodeSimilarityTemplate();
            case "LexicalTemplate":
                return new LexicalTemplate(validPOSTags, frequentWordsToExclude, semanticTypes);
            case "ResourceTemplate":
                return new ResourceTemplate(validPOSTags, semanticTypes);
            case "PropertyTemplate":
                return new PropertyTemplate(validPOSTags, semanticTypes);

        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
