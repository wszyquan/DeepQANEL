package de.citec.sc.parser;

import de.citec.sc.query.Search;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sherzod
 */
public class DependencyParse {

    private HashMap<Integer, String> nodes;
    private HashMap<Integer, Integer> relations;
    private HashMap<Integer, String> edgeStrings;
    private HashMap<Integer, String> POSTAG;

    private String treeString;
//    private List<Token> tokens;
    private int headNode;

    public DependencyParse() {
        nodes = new HashMap<Integer, String>();
        relations = new LinkedHashMap<>();
        edgeStrings = new HashMap<Integer, String>();
        POSTAG = new HashMap<Integer, String>();
//        this.tokens = new ArrayList<>();
    }

    public void mergeEdges() {

//        System.out.println("Before\n\n" + toString()+"\n");
        mergeCompountEdges();

//        System.out.println("After compound\n\n" + toString()+"\n");
        mergeAmodEdges();

//        System.out.println("After mergeAmodEdges\n\n" + toString()+"\n");
        mergeDepEdges();

//        System.out.println("After mergeDepEdges\n\n" + toString()+"\n");
        mergeDetEdges();

//        System.out.println("After mergeDetEdges\n\n" + toString()+"\n");
        mergePatterns();

//        System.out.println("After mergePatterns\n\n" + toString()+"\n");
    }

    private void mergePatterns() {

        List<String> patterns = new ArrayList<>();
//        patterns.add("NN IN NNP");//Battle (NN) 		5,of (IN) 		6,Gettysburg (NNP)
        patterns.add("NNP IN NNP");//Lawrence (NNP) 		7,of (IN) 		8,Arabia (NNP)
        patterns.add("NNP CC NNP");//Lawrence (NNP) 		7,of (IN) 		8,Arabia (NNP)
//        patterns.add("NNS IN NNP");//Houses (NNP) 		7,of (IN) 		8,Parliament (NNP)
        patterns.add("NN IN NNS");//nobel prize (NN) 		7,of (IN) 		8,physics (NNS)
        patterns.add("NNP CD");//7,Chile Route (NNP) 		8,68 (CD) 

        List<Integer> allNodes = new ArrayList<>();
        allNodes.addAll(nodes.keySet());

        //sort the nodes
        Collections.sort(allNodes);

        for (int i = 0; i < allNodes.size(); i++) {

            //get 1,2 token
            for (int r = 1; r <= 2; r++) {

                if (i + r < allNodes.size()) {

                    List<Integer> mergedNodes = allNodes.subList(i, i + r + 1);
                    String postags = "";
                    String mergedTokens = "";
                    for (Integer m : mergedNodes) {
                        postags += POSTAG.get(m) + " ";
                        mergedTokens += getToken(m) + " ";
                    }
                    postags = postags.trim();
                    mergedTokens = mergedTokens.trim();

                    if (patterns.contains(postags)) {

                        if (mergedTokens.isEmpty()) {
                            continue;
                        }
                        boolean b = Search.matches(mergedTokens.toLowerCase());

                        //if matches then remove nodes
                        if (b) {

                            for (Integer depNode : mergedNodes) {
                                if (depNode == allNodes.get(i)) {
                                    continue;
                                }

                                edgeStrings.remove(depNode);
                                relations.remove(depNode);
                                nodes.remove(depNode);
                                POSTAG.remove(depNode);
                            }

                            POSTAG.put(allNodes.get(i), "NNP");
                            nodes.put(allNodes.get(i), mergedTokens);
                        }
                    }
                }
            }

        }
    }

    private void mergeAmodEdges() {
        //do another merging on amod - Give me all Australian nonprofit organizations. --> merges nonprofit organizations
        int counter = 0;

        List<Integer> traversedDepNodes = new ArrayList<>();

        while (edgeStrings.containsValue("amod")) {

            counter++;
            Integer headNode = -1;

            List<Integer> depNodesWithCompoundEdge = new ArrayList<>();

            //get the head node
            for (Integer depNode : edgeStrings.keySet()) {
                if (traversedDepNodes.contains(depNode)) {
                    continue;
                }

                if (edgeStrings.get(depNode).equals("amod")) {
                    headNode = getParentNode(depNode);
                    String headPOS = getPOSTag(headNode);

//                    boolean isValidMerge = false;
//                    switch (headPOS) {
//                        case "NNP":
//                            if (depNode.equals("NNP") || depNode.equals("NNPS")) {
//                                isValidMerge = true;
//                            }
//                            break;
//                        case "NN":
//                            if (depNode.equals("NN") || depNode.equals("NNS")) {
//                                isValidMerge = true;
//                            }
//                            else if (depNode.equals("NNP") || depNode.equals("NNPS")) {
//                                isValidMerge = true;
//                            }
//                            break;
//                    }
//
//                    if (!isValidMerge) {
//                        continue;
//                    }
                    depNodesWithCompoundEdge.add(headNode);
                    depNodesWithCompoundEdge.add(depNode);

                    //not to continue with the same node
                    traversedDepNodes.add(depNode);
                    break;
                }
            }

            if (headNode == -1) {
                break;
            }
            if (counter == nodes.size()) {
                break;
            }

            Collections.sort(depNodesWithCompoundEdge);

            String mergedTokens = "";
            for (Integer nodeIndex : depNodesWithCompoundEdge) {
                if (nodes.get(nodeIndex) == null) {
                    continue;
                }
                if (nodes.get(nodeIndex).equals("null")) {
                    continue;
                }
                mergedTokens += nodes.get(nodeIndex) + " ";
            }
            mergedTokens = mergedTokens.trim();

            //check if there are any capital letters in the mergedTokens
            //if there are don't expand these tokens
//            boolean hasUppercase = !mergedTokens.equals(mergedTokens.toLowerCase());
//            
//            if(hasUppercase){
//                continue;
//            }
            if (mergedTokens.isEmpty()) {
                continue;
            }
            boolean b = Search.matches(mergedTokens);

            //if matches then remove nodes
            if (b) {

                for (Integer depNode : depNodesWithCompoundEdge) {
                    if (depNode == headNode) {
                        continue;
                    }

                    edgeStrings.remove(depNode);
                    relations.remove(depNode);
                    nodes.remove(depNode);
                    POSTAG.remove(depNode);

                }

                nodes.put(headNode, mergedTokens);
            }
        }
    }

    private void mergeDepEdges() {
        //do another merging on amod - Give me all Australian nonprofit organizations. --> merges nonprofit organizations
        int counter = 0;

        List<Integer> traversedDepNodes = new ArrayList<>();

        while (edgeStrings.containsValue("dep")) {

            counter++;
            Integer headNode = -1;

            List<Integer> depNodesWithCompoundEdge = new ArrayList<>();

            //get the head node
            for (Integer depNode : edgeStrings.keySet()) {
                if (traversedDepNodes.contains(depNode)) {
                    continue;
                }

                if (edgeStrings.get(depNode).equals("dep")) {
                    headNode = getParentNode(depNode);
                    String headPOS = getPOSTag(headNode);
                    String depPOS = getPOSTag(depNode);

                    boolean isValidMerge = false;
                    switch (headPOS) {
                        case "NNP":
                            if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NNPS":
                            if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NN":
                            if (depPOS.equals("NN") || depPOS.equals("NNS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NNS":
                            if (depPOS.equals("NN") || depPOS.equals("NNS")) {
                                isValidMerge = true;
                            }
                            break;
                    }

                    if (!isValidMerge) {
                        continue;
                    }
                    depNodesWithCompoundEdge.add(headNode);
                    depNodesWithCompoundEdge.add(depNode);

                    //not to continue with the same node
                    traversedDepNodes.add(depNode);
                    break;
                }
            }

            if (headNode == -1) {
                break;
            }
            if (counter == nodes.size()) {
                break;
            }

            Collections.sort(depNodesWithCompoundEdge);

            String mergedTokens = "";
            for (Integer nodeIndex : depNodesWithCompoundEdge) {
                if (nodes.get(nodeIndex) == null) {
                    continue;
                }
                if (nodes.get(nodeIndex).equals("null")) {
                    continue;
                }
                mergedTokens += nodes.get(nodeIndex) + " ";
            }
            mergedTokens = mergedTokens.trim();

            //check if there are any capital letters in the mergedTokens
            //if there are don't expand these tokens
//            boolean hasUppercase = !mergedTokens.equals(mergedTokens.toLowerCase());
//            
//            if(hasUppercase){
//                continue;
//            }
            if (mergedTokens.isEmpty()) {
                continue;
            }
            boolean b = Search.matches(mergedTokens);

            //if matches then remove nodes
            if (b) {

                for (Integer depNode : depNodesWithCompoundEdge) {
                    if (depNode == headNode) {
                        continue;
                    }

                    edgeStrings.remove(depNode);
                    relations.remove(depNode);
                    nodes.remove(depNode);
                    POSTAG.remove(depNode);

                }

                nodes.put(headNode, mergedTokens);
            }
        }
    }

    private void mergeDetEdges() {
        //do another merging on amod - Give me all Australian nonprofit organizations. --> merges nonprofit organizations
        int counter = 0;

        List<Integer> traversedDepNodes = new ArrayList<>();

        while (edgeStrings.containsValue("det")) {

            counter++;
            Integer headNode = -1;

            List<Integer> depNodesWithCompoundEdge = new ArrayList<>();

            //get the head node
            for (Integer depNode : edgeStrings.keySet()) {
                if (traversedDepNodes.contains(depNode)) {
                    continue;
                }

                if (edgeStrings.get(depNode).equals("det")) {
                    headNode = getParentNode(depNode);
                    String headToken = getToken(headNode);
                    String depToken = getToken(depNode);

                    //The Sopranos
                    boolean isHeadNodeUpperCase = !headToken.equals(headToken.toLowerCase());
                    boolean isDepNodeUpperCase = !depToken.equals(depToken.toLowerCase());

                    if (!(isDepNodeUpperCase || isHeadNodeUpperCase)) {
                        continue;
                    }

                    depNodesWithCompoundEdge.add(headNode);
                    depNodesWithCompoundEdge.add(depNode);

                    //not to continue with the same node
                    traversedDepNodes.add(depNode);
                    break;
                }
            }

            if (headNode == -1) {
                break;
            }
            if (counter == nodes.size()) {
                break;
            }

            Collections.sort(depNodesWithCompoundEdge);

            //add all indice between the maximum and the minimum value
            //Melon de Bourgogne = not to have sth like this :Melon Bourgogne
            List<Integer> missingIntervals = new ArrayList<>();

            for (int i = 0; i < depNodesWithCompoundEdge.size(); i++) {

                if (i + 1 < depNodesWithCompoundEdge.size()) {
                    Integer node = depNodesWithCompoundEdge.get(i);
                    Integer nextNode = depNodesWithCompoundEdge.get(i + 1);

                    //if there is some index missing
                    if (nextNode - node != 1) {
                        missingIntervals.add(node + 1);
                    }
                }
            }
            //add missing intervals and sort again
            if (!missingIntervals.isEmpty()) {
                depNodesWithCompoundEdge.addAll(missingIntervals);
                Collections.sort(depNodesWithCompoundEdge);
            }

            String mergedTokens = "";
            for (Integer nodeIndex : depNodesWithCompoundEdge) {
                if (nodes.get(nodeIndex) == null) {
                    continue;
                }
                if (nodes.get(nodeIndex).equals("null")) {
                    continue;
                }
                mergedTokens += nodes.get(nodeIndex) + " ";
            }
            mergedTokens = mergedTokens.trim();

            //check if there are any capital letters in the mergedTokens
            //if there are don't expand these tokens
//            boolean hasUppercase = !mergedTokens.equals(mergedTokens.toLowerCase());
//            
//            if(hasUppercase){
//                continue;
//            }
            if (mergedTokens.isEmpty()) {
                continue;
            }
            boolean b = Search.matches(mergedTokens);

            //if matches then remove nodes
            if (b) {

                for (Integer depNode : depNodesWithCompoundEdge) {
                    if (depNode == headNode) {
                        continue;
                    }

                    edgeStrings.remove(depNode);
                    relations.remove(depNode);
                    nodes.remove(depNode);
                    POSTAG.remove(depNode);

                }

                nodes.put(headNode, mergedTokens);
            }
        }
    }

    private void mergeCompountEdges() {

        int counter = 0;
        while (edgeStrings.containsValue("compound")) {

            if (counter == nodes.size()) {
                break;
            }
            counter++;

            Integer headNode = -1;
            String headPOS = "";

            //get the head node
            for (Integer depNode : edgeStrings.keySet()) {
                if (edgeStrings.get(depNode).equals("compound")) {
                    headNode = getParentNode(depNode);
                    headPOS = getPOSTag(headNode);
                    break;
                }
            }

            if (headNode == -1) {
                break;
            }

            List<Integer> depNodes = getDependentEdges(headNode);

            List<Integer> depNodesWithCompoundEdge = new ArrayList<>();

            //get all edges with comound
            for (Integer depNode : depNodes) {
                String depPOS = getPOSTag(depNode);

                if (edgeStrings.get(depNode).equals("compound")) {

                    boolean isValidMerge = false;
                    switch (headPOS) {
                        case "NNP":
                            if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            } else if (depPOS.equals("NN") || depPOS.equals("NNS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NNPS":
                            if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NN":
                            if (depPOS.equals("NN") || depPOS.equals("NNS")) {
                                isValidMerge = true;
                            } else if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            }
                            break;
                        case "NNS":
                            if (depPOS.equals("NN") || depPOS.equals("NNS")) {
                                isValidMerge = true;
                            } else if (depPOS.equals("NNP") || depPOS.equals("NNPS")) {
                                isValidMerge = true;
                            }
                            break;
                    }

                    if (!isValidMerge) {
                        continue;
                    }

                    depNodesWithCompoundEdge.add(depNode);

                    //remove the edge from the map
                    edgeStrings.remove(depNode);

                    relations.remove(depNode);

                    POSTAG.remove(depNode);
                }
            }

            //add the head node too
            depNodesWithCompoundEdge.add(headNode);
            Collections.sort(depNodesWithCompoundEdge);

            //add all indice between the maximum and the minimum value
            //Melon de Bourgogne = not to have sth like this :Melon Bourgogne
            List<Integer> missingIntervals = new ArrayList<>();

            for (int i = 0; i < depNodesWithCompoundEdge.size(); i++) {

                if (i + 1 < depNodesWithCompoundEdge.size()) {
                    Integer node = depNodesWithCompoundEdge.get(i);
                    Integer nextNode = depNodesWithCompoundEdge.get(i + 1);

                    //if there is some index missing
                    if (nextNode - node != 1) {
                        missingIntervals.add(node + 1);
                    }
                }
            }
            //add missing intervals and sort again
            if (!missingIntervals.isEmpty()) {
                depNodesWithCompoundEdge.addAll(missingIntervals);
                Collections.sort(depNodesWithCompoundEdge);
            }

            String mergedTokens = "";
            for (Integer nodeIndex : depNodesWithCompoundEdge) {
                if (nodes.get(nodeIndex) == null) {
                    continue;
                }
                if (nodes.get(nodeIndex).equals("null")) {
                    continue;
                }

                mergedTokens += nodes.get(nodeIndex) + " ";

                if (nodeIndex == headNode) {
                    continue;
                }

                //remove the edge from the map
                edgeStrings.remove(nodeIndex);

                relations.remove(nodeIndex);

                POSTAG.remove(nodeIndex);
                //remove the token string as well
                nodes.remove(nodeIndex);
            }
            mergedTokens = mergedTokens.trim();

            if (mergedTokens.isEmpty()) {
                continue;
            }

            nodes.put(headNode, mergedTokens);

        }

    }

    /**
     * removes loops from edges that linking to itself
     */
    public void removeLoops() {
        boolean isLoop = true;

        while (isLoop) {
            Integer loopRelationKey = -1;
            Integer loopRelationValue = -1;

            for (Integer k : getRelations().keySet()) {
                Integer v = getRelations().get(k);

                if (k.equals(v)) {
                    isLoop = true;

                    loopRelationKey = k;
                    loopRelationValue = v;
                    break;
                }
                //check if any relations has v as Key, and k as Value

                if (getRelations().containsKey(v)) {

                    Integer otherK = getRelations().get(v);

                    if (k.equals(otherK)) {
                        isLoop = true;
                        
                        loopRelationKey = k;
                        loopRelationValue = v;
                        break;
                    }
                }
            }

            if (loopRelationKey != -1 && loopRelationValue != -1) {
                System.out.println("Removed loop from edge: " +loopRelationKey +", "+ loopRelationValue);
                relations.remove(loopRelationKey, loopRelationValue);
            } else {
                isLoop = false;
            }

        }
    }

    public void addNode(int index, String label, String pos, int beginPosition, int endPosition) {
        if (!nodes.containsKey(index)) {
            nodes.put(new Integer(index), label);
            POSTAG.put(new Integer(index), pos);

//            Token t = new Token(index, beginPosition, endPosition, label);
//            tokens.add(t);
        }
//            else {
//            if (nodes.get(index).length() < label.length()) {
//
//                for (Token t1 : tokens) {
//                    if (t1.getText().equals(nodes.get(index))) {
//                        tokens.remove(t1);
//                        break;
//                    }
//                }
//                Token newToken = new Token(index, beginPosition, endPosition, label);
//
//                tokens.add(newToken);
//                nodes.put(new Integer(index), label);
//                POSTAG.put(new Integer(index), pos);
//            }
//        }
    }

    public void addEdge(int dependent, int head, String depRelation) {
        this.relations.put(new Integer(dependent), new Integer(head));
        this.edgeStrings.put(new Integer(dependent), depRelation);
    }

    @Override
    public String toString() {

        String r = "\nNodes:\n";
        for (int s : nodes.keySet()) {
            r += "\t" + s + "," + nodes.get(s) + " (" + POSTAG.get(s) + ") \t";
        }

        r += "\nEdges:\n";
        for (int s : edgeStrings.keySet()) {
            r += "\t" + s + "," + edgeStrings.get(s) + "\t";
        }

        r += "\n\nParse Tree:\n";
        for (int s : relations.keySet()) {
            r += " (" + s + "," + relations.get(s) + ")\t";
        }

        r += "\nHead node: " + headNode;
        return r;
    }

    public List<String> getWords() {
        return (List<String>) nodes.values();
    }

    /**
     * returns dependent edges given the headNode
     *
     * @param headNode
     * @return List of dependent nodes
     */
    public List<Integer> getDependentEdges(int headNode) {

        List<Integer> list = new ArrayList<>();
        for (Integer k : relations.keySet()) {
            Integer v = relations.get(k);

            if (v == headNode) {
                list.add(k);
            }
        }

        return list;
    }

    /**
     * returns dependent edges given the headNode that have a valid postag
     *
     * @param headNode
     * @param acceptedPOSTAGs set of postags
     * @return List of dependent nodes
     */
    public List<Integer> getDependentEdges(int headNode, Set<String> acceptedPOSTAGs, Set<String> frequentWordsToExclude) {

        List<Integer> list = new ArrayList<>();
        for (Integer k : relations.keySet()) {
            Integer v = relations.get(k);

            if (v == headNode) {
                String postag = getPOSTag(k);

                if (!acceptedPOSTAGs.contains(postag)) {
                    continue;
                }

                String token = getToken(k);

                if (frequentWordsToExclude.contains(token.toLowerCase())) {
                    continue;
                }

                list.add(k);

            }
        }

        return list;
    }

    /**
     * returns sibling edges of given the headNode if the postag of that is in
     * acceptedPOSTAGs
     *
     * @param headNode
     * @return List of sibling nodes
     */
    public List<Integer> getSiblings(int nodeId, Set<String> acceptedPOSTAGs, Set<String> frequentWordsToExclude) {

        List<Integer> list = new ArrayList<>();

        Integer parentNode = getParentNode(nodeId);

        if (parentNode != null) {
            List<Integer> allChildren = getDependentEdges(parentNode);

            for (Integer s : allChildren) {

                String postag = getPOSTag(s);

                if (!acceptedPOSTAGs.contains(postag)) {
                    continue;
                }

                String token = getToken(s);

                if (frequentWordsToExclude.contains(token.toLowerCase())) {
                    continue;
                }

                if (!s.equals(nodeId)) {
                    list.add(s);
                }

            }
        }

        return list;
    }

    /**
     * returns sibling edges of given the headNode
     *
     * @param headNode
     * @return List of dependent nodes
     */
    public List<Integer> getSiblings(int nodeId) {

        List<Integer> list = new ArrayList<>();

        Integer parentNode = getParentNode(nodeId);

        if (parentNode != null) {
            List<Integer> allChildren = getDependentEdges(parentNode);

            for (Integer s : allChildren) {
                if (!s.equals(nodeId)) {
                    list.add(s);
                }
            }
        }

        return list;
    }

    /**
     * returns parent node of given the node
     *
     * @param node
     * @return parent node
     */
    public Integer getParentNode(int node) {

        if (relations.containsKey(node)) {
            Integer parentNode = relations.get(node);

            return parentNode;
        }
        return -1;

    }

    public HashMap<Integer, String> getNodes() {
        return nodes;
    }

    /**
     * returns relations between nodes in the parse tree
     *
     * return HashMap<Integer, Integer> where keys are dependent nodes and
     * values are parent nodes
     *
     * @return HashMap<Integer, Integer> relations
     */
    public HashMap<Integer, Integer> getRelations() {

        HashMap<Integer, Integer> edges = new LinkedHashMap<>();
        for (Integer k : relations.keySet()) {
            edges.put(k, relations.get(k));
        }
        return edges;
    }

    private HashMap<Integer, String> getEdgeStrings() {
        return edgeStrings;
    }

    /**
     * returns dependency relation for the given dependent node if given
     * dependent node is the root of the parse tree then returns
     * "ThisNodeIsRoot"
     *
     * @param dependentNodeId
     * @return String dependency relation
     */
    public String getDependencyRelation(Integer dependentNodeId) {
        if (edgeStrings.containsKey(dependentNodeId)) {
            return edgeStrings.get(dependentNodeId);
        }
        return "ThisNodeIsRoot";
    }

    /**
     * returns postag for the given node
     *
     * @param dependentNodeId
     * @return String POSTag
     */
    public String getPOSTag(Integer nodeId) {
        return POSTAG.get(nodeId);
    }

    /**
     * returns token for the given node
     *
     * @param dependentNodeId
     * @return String token
     */
    public String getToken(Integer nodeId) {
        return nodes.get(nodeId);
    }

    public int getHeadNode() {
        return headNode;
    }

    public void setHeadNode(int headNode) {
        this.headNode = headNode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.nodes);
        hash = 29 * hash + Objects.hashCode(this.relations);
        hash = 29 * hash + Objects.hashCode(this.edgeStrings);
        hash = 29 * hash + Objects.hashCode(this.POSTAG);
        hash = 29 * hash + Objects.hashCode(this.treeString);
//        hash = 29 * hash + Objects.hashCode(this.tokens);
        hash = 29 * hash + this.headNode;
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
        final DependencyParse other = (DependencyParse) obj;
        if (!Objects.equals(this.nodes, other.nodes)) {
            return false;
        }
        if (!Objects.equals(this.relations, other.relations)) {
            return false;
        }
        if (!Objects.equals(this.edgeStrings, other.edgeStrings)) {
            return false;
        }
        if (!Objects.equals(this.POSTAG, other.POSTAG)) {
            return false;
        }
        if (!Objects.equals(this.treeString, other.treeString)) {
            return false;
        }
//        if (!Objects.equals(this.tokens, other.tokens)) {
//            return false;
//        }
        if (this.headNode != other.headNode) {
            return false;
        }
        return true;
    }

//    public HashMap<Integer, String> getPOSTAG() {
//        return POSTAG;
//    }
//    public List<Token> getTokens() {
//        return tokens;
//    }
    public String getTreeString() {
        return treeString;
    }

    public void setTreeString(String treeString) {
        this.treeString = treeString;
    }

}
