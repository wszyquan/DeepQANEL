/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ricecode.similarity;

/**
 *
 * @author sherzod
 */
public class StringSimilarityMeasures implements SimilarityStrategy {

    private LevenshteinDistanceStrategy l;
    private DiceCoefficientStrategy dc;

    public StringSimilarityMeasures() {
        l = new LevenshteinDistanceStrategy();
        dc = new DiceCoefficientStrategy();
    }

    /**
     * @param first
     * @param second
     * 
     * computes F1 measure of DiceCoefficient Similarity and Levenshtein Distance Similarity combined given two strings
     * 
     * @return double score
     */
    public double score(String first, String second) {
        double s1 = l.score(first, second);

        double s2 = dc.score(first, second);

        double f1 =  (2 * s1 * s2) / (s1 + s2);
        
        if(Double.isNaN(f1)){
            f1 = 0;
        }

        return s1;
    }

}
