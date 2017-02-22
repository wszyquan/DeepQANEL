/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.main;

import de.citec.sc.query.Candidate;
import de.citec.sc.query.ManualLexicon;
import de.citec.sc.query.Search;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *
 * @author sherzod
 */
public class SearchTerms {

    public static void main(String[] args) {

        Search.useMatoll(true);

        ManualLexicon.useManualLexicon(true);

        Search indexSearch = new Search();

        String word = "created";
        int topK = 100;
        boolean lemmatize = true;
        boolean useWordNet = false;
        boolean mergePartialMatches = false;

        System.out.println(ManualLexicon.getClasses(word));

        Set<Candidate> result = new LinkedHashSet<>();

        long start = System.currentTimeMillis();
        result.addAll(indexSearch.getResources(word, topK, lemmatize, mergePartialMatches, useWordNet));
        System.out.println("Resources: \n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();

        System.out.println("======================================\nProperties:\n DBpedia + MATOLL");
        result.addAll(indexSearch.getPredicates(word, topK, lemmatize, mergePartialMatches, useWordNet));
        result.forEach(System.out::println);
        result.clear();

        result = new LinkedHashSet<>();

        result.addAll(indexSearch.getClasses(word, topK, lemmatize, mergePartialMatches, useWordNet));
        System.out.println("======================================\nClasses:\n");
        result.forEach(System.out::println);

        result = new LinkedHashSet<>();

        result.addAll(indexSearch.getRestrictionClasses(word, topK, lemmatize, mergePartialMatches, useWordNet));
        System.out.println("======================================\nRestriction Classes:\n");
        result.forEach(System.out::println);

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");

    }
}
