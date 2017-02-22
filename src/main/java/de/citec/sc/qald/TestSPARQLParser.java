/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.qald;

import java.util.List;

/**
 *
 * @author sherzod
 */
public class TestSPARQLParser {
    public static void main(String[] args) {
        List<String> uris = SPARQLParser.extractURIsFromQuery("SELECT DISTINCT ?uri WHERE {  <http://dbpedia.org/resource/Wikipedia> <http://dbpedia.org/ontology/author> ?uri . } ");
        
        System.out.println(uris);
    }
}
