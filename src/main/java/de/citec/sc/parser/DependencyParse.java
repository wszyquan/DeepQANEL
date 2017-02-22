package de.citec.sc.parser;


import de.citec.sc.query.Search;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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

    public void mergeEdges(Search index) {

        mergeCompountEdges(index);

        mergeAmodEdges(index);

        mergePatterns(index);

    }

    private void mergePatterns(Search index) {

        List<String> patterns = new ArrayList<>();
//        patterns.add("NN IN NNP");//Battle (NN) 		5,of (IN) 		6,Gettysburg (NNP)
        patterns.add("NNP IN NNP");//Lawrence (NNP) 		7,of (IN) 		8,Arabia (NNP)
        patterns.add("NNP CC NNP");//Lawrence (NNP) 		7,of (IN) 		8,Arabia (NNP)
//        patterns.add("NNS IN NNP");//Houses (NNP) 		7,of (IN) 		8,Parliament (NNP)

        List<Integer> allNodes = new ArrayList<>();
        allNodes.addAll(nodes.keySet());

        //sort the nodes
        Collections.sort(allNodes);

        for (int i = 0; i < allNodes.size(); i++) {

            if (i + 2 < allNodes.size()) {
                List<Integer> mergedNodes = allNodes.subList(i, i + 3);
                String postags = "";
                String mergedTokens = "";
                for (Integer m : mergedNodes) {
                    postags += POSTAG.get(m) + " ";
                    mergedTokens += getToken(m) + " ";
                }
                postags = postags.trim();
                mergedTokens = mergedTokens.trim();

                if (patterns.contains(postags)) {

                    boolean b = index.matches(mergedTokens.toUpperCase());

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

    private void mergeAmodEdges(Search index) {
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
            boolean b = index.matches(mergedTokens);

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

    private void mergeCompountEdges(Search index) {

        while (edgeStrings.containsValue("compound")) {

            Integer headNode = -1;

            //get the head node
            for (Integer depNode : edgeStrings.keySet()) {
                if (edgeStrings.get(depNode).equals("compound")) {
                    headNode = getParentNode(depNode);
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
                if (edgeStrings.get(depNode).equals("compound")) {
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

            nodes.put(headNode, mergedTokens);

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
