/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.utils;

/**
 *
 * @author sherzod
 */
public class TestDBpediaEndPoint {
    public static void main(String[] args) {
        System.out.println(DBpediaEndpoint.getRange("http://dbpedia.org/ontology/birthPlace"));
    }
}
