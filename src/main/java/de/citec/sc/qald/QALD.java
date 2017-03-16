/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.qald;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author sherzod
 */
public class QALD {

    public static void save(String[] args) {
        ArrayList<Question> q = getQuestions("src/main/resources/qald_test.xml");

        String query = "QUERY";
        List<String> answers = new ArrayList<>();
        answers.add("ANSWER#1");
        answers.add("ANSWER#2");

        writeQuestions("src/main/resources/qald-5_train.xml", q);
        int a = 1;
    }

    public static void writeQuestions(String fileName, List<Question> parsedQuestions) {
        try {

            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            ArrayList<Question> questions = new ArrayList<Question>();

            NodeList nList = doc.getElementsByTagName("question");

            int count = 0;
            ArrayList<String> tempList = new ArrayList<>();

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                Element element = (Element) nNode;
                String question = getTagValue("string", element);

                String queryString = "";
                List<String> answersList = new ArrayList<>();

                for (Question q1 : parsedQuestions) {
                    if (q1.getQuestionText().equalsIgnoreCase(question)) {
                        if (!q1.getQueryText().equals("")) {
                            queryString = q1.getQueryText();
                        }
                        if (q1.getAnswers() != null) {
                            answersList = q1.getAnswers();
                        }

                        break;
                    }
                }

                Element query = doc.createElement("query");
                CDATASection cdata = doc.createCDATASection(queryString);
                query.appendChild(cdata);
                element.appendChild(query);

                Element answers = doc.createElement("answers");
                for (String a : answersList) {

                    Element node = doc.createElement("answer");
                    node.appendChild(doc.createTextNode(a));

                    answers.appendChild(node);
                }
                element.appendChild(answers);

                //System.out.println("\nXML DOM Created Successfully..");
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            Result output = new StreamResult(new File("src/main/resources/output.xml"));

            transformer.transform(source, output);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static ArrayList<Question> getQuestions(String fileName) {
        try {

            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            ArrayList<Question> questions = new ArrayList<Question>();

            NodeList nList = doc.getElementsByTagName("question");

            System.out.println(nList.getLength());

            int count = 0;
            ArrayList<String> tempList = new ArrayList<>();

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                boolean isAdded = false;
                String onlyDBO = "", aggregation = "", answerType = "", hybrid = "";
                String id = "";

                if (nNode.getAttributes().getNamedItem("onlydbo") != null
                        && nNode.getAttributes().getNamedItem("aggregation") != null
                        && nNode.getAttributes().getNamedItem("answertype") != null
                        && nNode.getAttributes().getNamedItem("hybrid") != null) {

                    onlyDBO = nNode.getAttributes().getNamedItem("onlydbo").getTextContent();
                    aggregation = nNode.getAttributes().getNamedItem("aggregation").getTextContent();
                    answerType = nNode.getAttributes().getNamedItem("answertype").getTextContent();
                    hybrid = nNode.getAttributes().getNamedItem("hybrid").getTextContent();
                }

                if (nNode.getAttributes().getNamedItem("id") != null) {
                    String idd = nNode.getAttributes().getNamedItem("id").getTextContent();
                    id = idd;
                }

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String query = "", question = "", keywords = "";

                    try {
                        if (hybrid.equals("true")) {
                            question = getTagValue("string", eElement);
                            query = getTagValue("pseudoquery", eElement);
                        } else {
                            question = getTagValue("string", eElement);
                            keywords = getTagValue("keywords", eElement);
                            query = getTagValue("query", eElement);
                        }

                    } catch (Exception e) {
                        System.err.println("Error parsing: " + id);
                    }

                    if (!question.equals("") && !query.equals("")) {
                        if (!query.contains("OUT OF SCOPE")) {
                            tempList.add(question);
                            isAdded = true;
                        }

                    }

                    Question q1 = new Question(question, query, onlyDBO, aggregation, answerType, hybrid, id);

                    if (!query.contains("OUT OF SCOPE")) {
                        if (!question.equals("") && !query.equals("")) {
                            questions.add(q1);
                        }
                    }
                }
            }
            return questions;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Question> getTestQuestions(String fileName) {
        try {

            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            ArrayList<Question> questions = new ArrayList<Question>();

            NodeList nList = doc.getElementsByTagName("question");

            int count = 0;
            ArrayList<String> tempList = new ArrayList<>();

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                boolean isAdded = false;
                String onlyDBO = "", aggregation = "", answerType = "";
                String id = "";

                if (nNode.getAttributes().getNamedItem("onlydbo") != null && nNode.getAttributes().getNamedItem("aggregation") != null && nNode.getAttributes().getNamedItem("answertype") != null) {
                    onlyDBO = nNode.getAttributes().getNamedItem("onlydbo").getTextContent();
                    aggregation = nNode.getAttributes().getNamedItem("aggregation").getTextContent();
                    answerType = nNode.getAttributes().getNamedItem("answertype").getTextContent();
                }

                if (nNode.getAttributes().getNamedItem("id") != null) {
                    String idd = nNode.getAttributes().getNamedItem("id").getTextContent();
                    id = idd;
                }

                if (onlyDBO.equals("true") || onlyDBO.equals("false")) {
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        String query = getTagValue("query", eElement);
                        String question = getTagValue("string", eElement);
                        if (!question.equals("") && !query.equals("")) {
                            tempList.add(question);
                            isAdded = true;
                        }

                        Question q1 = new Question(question, query, id);
                        questions.add(q1);

                    }
                }
            }

            return questions;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTagValue(String sTag, Element eElement) {

        NodeList temp = eElement.getElementsByTagName(sTag);

        if (temp.item(0) == null) {
            return "";

        } else {
            NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

            Node nValue = (Node) nlList.item(0);

            return nValue.getNodeValue();
        }

    }
}
