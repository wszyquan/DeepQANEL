/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.parser;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args) {
        StanfordParser parser = new StanfordParser();

        DependencyParse parse = parser.parse("Which companies work in the aerospace industry as well as in medicine?");

        System.out.println(parse);

        boolean isLoop = false;
        String loop = "";
        for (Integer k : parse.getRelations().keySet()) {
            Integer v = parse.getRelations().get(k);

            if (k.equals(v)) {
                isLoop = true;
                loop = k + " " + v;
                break;
            }
            //check if any relations has v as Key, and k as Value

            if (parse.getRelations().containsKey(v)) {

                Integer otherK = parse.getRelations().get(v);

                if (k.equals(otherK)) {
                    isLoop = true;
                    loop = k + " " + v;
                    break;
                }
            }
        }

        System.out.println(loop);

        //parser.testParseTree();
//        try {
//            
//            System.out.println(parser.identifyNER("Who visited New York?"));
//            
//        } catch (Exception e) {
//        }
//        String s = "Give me a list of all bandleaders that play trumpet.";
//        DependencyParse parse = parser.parse(s);
//        
//        System.out.print(parse);
    }
}
