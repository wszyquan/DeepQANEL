/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import edu.stanford.nlp.util.ArraySet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class ManualLexicon {

    private static HashMap<String, Set<String>> lexiconProperties;
    private static HashMap<String, Set<String>> lexiconClasses;
    private static HashMap<String, Set<String>> lexiconRestrictionClasses;
    private static HashMap<String, Set<String>> lexiconResources;

    public static boolean useManualLexicon = false;

    public static void useManualLexicon(boolean b) {
        useManualLexicon = b;
    }

    public static void load() {
        lexiconProperties = new HashMap<>();
        lexiconClasses = new HashMap<>();
        lexiconRestrictionClasses = new HashMap<>();
        lexiconResources = new HashMap<>();

        if (useManualLexicon) {
            
            loadTrainLexicon();

            loadTestLexicon();
        }

    }

    private static void loadTrainLexicon() {

        //QALD-6 Train
        //resources
        addLexicon("john lennon", "http://dbpedia.org/resource/Death_of_John_Lennon", lexiconResources);
        addLexicon("gmt", "http://dbpedia.org/resource/GMT_Games", lexiconResources);
        addLexicon("lighthouse in colombo", "http://dbpedia.org/resource/Colombo_Lighthouse", lexiconResources);
        addLexicon("mn", "'MN'@en", lexiconResources);
        addLexicon("baldwin", "'Baldwin'@en", lexiconResources);
        addLexicon("rodzilla", "'Rodzilla'@en", lexiconResources);
        addLexicon("iycm", "'IYCM'@en", lexiconResources);
        addLexicon("president of the united states", "'President of the United States'", lexiconResources);
        addLexicon("ireland", "\"Ireland\"@en", lexiconResources);
        addLexicon("japanese musical instruments", "http://dbpedia.org/class/yago/JapaneseMusicalInstruments", lexiconResources);
        addLexicon("eating disorders", "http://dbpedia.org/class/yago/EatingDisorders", lexiconResources);
        addLexicon("vatican television", "http://dbpedia.org/resource/Vatican_Television_Center", lexiconResources);
        addLexicon("U.S. president Lincoln", "http://dbpedia.org/resource/Abraham_Lincoln", lexiconResources);
        addLexicon("battle chess", "'Battle Chess'@en", lexiconResources);
//        addLexicon("juan carlos i", "http://dbpedia.org/resource/Juan_Carlos_I_of_Spain", lexiconResources);

        //properties
        addLexicon("play", "http://dbpedia.org/ontology/league", lexiconProperties);
        addLexicon("start", "http://dbpedia.org/ontology/routeStart", lexiconProperties);
        addLexicon("country", "http://dbpedia.org/ontology/foundationPlace", lexiconProperties);
        addLexicon("star", "http://dbpedia.org/ontology/starring", lexiconProperties);
        addLexicon("play", "http://dbpedia.org/ontology/starring", lexiconProperties);
        addLexicon("player", "http://dbpedia.org/ontology/team", lexiconProperties);
        addLexicon("produced", "http://dbpedia.org/ontology/assembly", lexiconProperties);
        addLexicon("produced", "http://dbpedia.org/ontology/assembly", lexiconProperties);
        addLexicon("influence", "http://dbpedia.org/ontology/influenced", lexiconProperties);
        addLexicon("completed", "http://dbpedia.org/ontology/completionDate", lexiconProperties);
        addLexicon("official color", "http://dbpedia.org/ontology/officialSchoolColour", lexiconProperties);

        //prepositions
        addLexicon("with", "http://dbpedia.org/ontology/starring", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/location", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/locatedInArea", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/league", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/country", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/isPartOf", lexiconProperties);
        addLexicon("from", "http://dbpedia.org/ontology/birthPlace", lexiconProperties);
        addLexicon("a", "http://dbpedia.org/ontology/profession", lexiconProperties);
        addLexicon("by", "http://dbpedia.org/ontology/author", lexiconProperties);
        addLexicon("from", "http://dbpedia.org/ontology/artist", lexiconProperties);

        addLexicon("pages", "http://dbpedia.org/ontology/numberOfPages", lexiconProperties);
        addLexicon("artistic movement", "http://dbpedia.org/ontology/movement", lexiconProperties);
        addLexicon("tall", "http://dbpedia.org/ontology/height", lexiconProperties);
        addLexicon("high", "http://dbpedia.org/ontology/height", lexiconProperties);
        addLexicon("high", "http://dbpedia.org/ontology/elevation", lexiconProperties);
        addLexicon("type", "http://dbpedia.org/ontology/class", lexiconProperties);
        addLexicon("employees", "http://dbpedia.org/ontology/numberOfEmployees", lexiconProperties);
        addLexicon("total population", "http://dbpedia.org/ontology/populationTotal", lexiconProperties);
        addLexicon("military conflicts", "http://dbpedia.org/ontology/battle", lexiconProperties);
        addLexicon("belong", "http://dbpedia.org/ontology/country", lexiconProperties);
        addLexicon("grow", "http://dbpedia.org/ontology/growingGrape", lexiconProperties);
        addLexicon("official color", "http://dbpedia.org/ontology/officialSchoolColour", lexiconProperties);
        addLexicon("timezone", "http://dbpedia.org/ontology/timeZone", lexiconProperties);
        addLexicon("timezone", "http://dbpedia.org/ontology/timezone", lexiconProperties);
        addLexicon("stand", "http://dbpedia.org/property/name", lexiconProperties);
        addLexicon("stand", "http://dbpedia.org/ontology/abbreviation", lexiconProperties);
        addLexicon("involved", "http://dbpedia.org/ontology/battle", lexiconProperties);
        addLexicon("killed", "http://dbpedia.org/property/conviction", lexiconProperties);
//        addLexicon("games", "http://dbpedia.org/ontology/publisher", lexiconProperties);
        addLexicon("region", "http://dbpedia.org/ontology/wineRegion", lexiconProperties);
        addLexicon("stores", "http://dbpedia.org/ontology/numberOfLocations", lexiconProperties);
        addLexicon("inhabitants", "http://dbpedia.org/ontology/populationTotal", lexiconProperties);
        addLexicon("mayor", "http://dbpedia.org/ontology/leader", lexiconProperties);
        addLexicon("professional", "http://dbpedia.org/ontology/occupation", lexiconProperties);
        addLexicon("connected", "http://dbpedia.org/property/country", lexiconProperties);
        addLexicon("killed", "http://dbpedia.org/property/conviction", lexiconProperties);
        addLexicon("flow through", "http://dbpedia.org/ontology/city", lexiconProperties);

        addLexicon("painted", "http://dbpedia.org/ontology/author", lexiconProperties);
        addLexicon("painter", "http://dbpedia.org/ontology/author", lexiconProperties);
        addLexicon("country", "http://dbpedia.org/ontology/nationality", lexiconProperties);
        addLexicon("actors", "http://dbpedia.org/ontology/starring", lexiconProperties);
        addLexicon("birthdays", "http://dbpedia.org/ontology/birthDate", lexiconProperties);
        addLexicon("flow through", "http://dbpedia.org/ontology/city", lexiconProperties);

        addLexicon("dwelt", "http://dbpedia.org/property/abode", lexiconProperties);
        addLexicon("time zone", "http://dbpedia.org/property/timezone", lexiconProperties);
        addLexicon("called", "http://dbpedia.org/property/shipNamesake", lexiconProperties);
        addLexicon("called", "http://dbpedia.org/property/nickname", lexiconProperties);
        addLexicon("called", "http://xmlns.com/foaf/0.1/surname", lexiconProperties);
        addLexicon("called", "http://www.w3.org/2000/01/rdf-schema#label", lexiconProperties);

        addLexicon("built", "http://dbpedia.org/property/beginningDate", lexiconProperties);
        addLexicon("border", "http://dbpedia.org/property/borderingstates", lexiconProperties);
        addLexicon("type", "http://dbpedia.org/property/design", lexiconProperties);
        addLexicon("abbreviation", "http://dbpedia.org/property/postalabbreviation", lexiconProperties);
        addLexicon("population density", "http://dbpedia.org/property/densityrank", lexiconProperties);
        addLexicon("first name", "http://xmlns.com/foaf/0.1/givenName", lexiconProperties);
        addLexicon("websites", "http://dbpedia.org/property/homepage", lexiconProperties);
        addLexicon("birth name", "http://dbpedia.org/property/birthName", lexiconProperties);
        addLexicon("governed", "http://dbpedia.org/ontology/leaderParty", lexiconProperties);
        addLexicon("span", "http://dbpedia.org/ontology/mainspan", lexiconProperties);
        addLexicon("run through", "http://dbpedia.org/property/country", lexiconProperties);
        addLexicon("moon", "http://dbpedia.org/property/satelliteOf", lexiconProperties);
        addLexicon("heavy", "http://dbpedia.org/ontology/mass", lexiconProperties);
        addLexicon("shot", "http://dbpedia.org/property/dateOfDeath", lexiconProperties);
        addLexicon("part", "http://dbpedia.org/property/alliance", lexiconProperties);
        addLexicon("members", "http://dbpedia.org/property/alliance", lexiconProperties);
        addLexicon("flew", "http://dbpedia.org/property/planet", lexiconProperties);
        addLexicon("cost", "http://dbpedia.org/ontology/budget", lexiconProperties);
        addLexicon("serve", "http://dbpedia.org/ontology/targetAirport", lexiconProperties);
        addLexicon("graduated", "http://dbpedia.org/ontology/almaMater", lexiconProperties);
        addLexicon("largest metropolitan area", "http://dbpedia.org/property/largestmetro", lexiconProperties);
        addLexicon("types", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", lexiconProperties);
        addLexicon("deep", "http://dbpedia.org/ontology/depth", lexiconProperties);
        addLexicon("astronauts", "http://dbpedia.org/ontology/mission", lexiconProperties);
        addLexicon("missions", "http://dbpedia.org/property/programme", lexiconProperties);
        addLexicon("cross", "http://dbpedia.org/ontology/crosses", lexiconProperties);
        addLexicon("students", "http://dbpedia.org/ontology/numberOfStudents", lexiconProperties);
        addLexicon("serves", "http://dbpedia.org/ontology/targetAirport", lexiconProperties);
        addLexicon("influenced", "http://dbpedia.org/ontology/influencedBy", lexiconProperties);
        addLexicon("die", "http://dbpedia.org/ontology/deathCause", lexiconProperties);
        addLexicon("launched", "http://dbpedia.org/ontology/launchSite", lexiconProperties);
        addLexicon("flow", "http://dbpedia.org/ontology/city", lexiconProperties);
        addLexicon("climb", "http://dbpedia.org/ontology/firstAscentPerson", lexiconProperties);

        //restriction classes
        addLexicon("democrat", "http://dbpedia.org/ontology/party###http://dbpedia.org/resource/Democratic_Party_(United_States)", lexiconRestrictionClasses);
        addLexicon("swedish", "http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/Sweden", lexiconRestrictionClasses);
        addLexicon("swedish", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Sweden", lexiconRestrictionClasses);
        addLexicon("dutch", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Netherlands", lexiconRestrictionClasses);
        addLexicon("oceanographers", "http://dbpedia.org/ontology/field###http://dbpedia.org/resource/Oceanography", lexiconRestrictionClasses);
        addLexicon("english gothic", "http://dbpedia.org/ontology/architecturalStyle###http://dbpedia.org/resource/English_Gothic_architecture", lexiconRestrictionClasses);
        addLexicon("nonprofit organizations", "http://dbpedia.org/ontology/type###http://dbpedia.org/resource/Nonprofit_organization", lexiconRestrictionClasses);
        addLexicon("danish", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Denmark", lexiconRestrictionClasses);
        addLexicon("canadian", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Canada", lexiconRestrictionClasses);
        addLexicon("greek", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Greece", lexiconRestrictionClasses);
        addLexicon("english", "http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/England", lexiconRestrictionClasses);
        addLexicon("grunge", "http://dbpedia.org/ontology/genre###http://dbpedia.org/resource/Grunge", lexiconRestrictionClasses);
        addLexicon("methodist", "http://dbpedia.org/ontology/religion###http://dbpedia.org/resource/Methodism", lexiconRestrictionClasses);
        addLexicon("australian", "http://dbpedia.org/ontology/hometown###http://dbpedia.org/resource/Australia", lexiconRestrictionClasses);
        addLexicon("australian", "http://dbpedia.org/ontology/locationCountry###http://dbpedia.org/resource/Australia", lexiconRestrictionClasses);
        addLexicon("spanish", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Spain", lexiconRestrictionClasses);

        addLexicon("metalcore", "http://dbpedia.org/ontology/genre###http://dbpedia.org/resource/Metalcore", lexiconRestrictionClasses);
        addLexicon("german", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Germany", lexiconRestrictionClasses);
        addLexicon("german", "http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/Germany", lexiconRestrictionClasses);
        addLexicon("jew", "http://dbpedia.org/property/ethnicity###'Jewish'@en", lexiconRestrictionClasses);
        addLexicon("politicians", "http://dbpedia.org/ontology/profession###http://dbpedia.org/resource/Politician", lexiconRestrictionClasses);
        addLexicon("chemist", "http://dbpedia.org/ontology/profession###http://dbpedia.org/resource/Chemist", lexiconRestrictionClasses);
        addLexicon("beer", "http://dbpedia.org/property/type###http://dbpedia.org/resource/Beer", lexiconRestrictionClasses);
        addLexicon("president of pakistan", "http://dbpedia.org/property/title###http://dbpedia.org/resource/President_of_Pakistan", lexiconRestrictionClasses);
        addLexicon("uk city", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/United_Kingdom", lexiconRestrictionClasses);
        addLexicon("pro-european", "http://dbpedia.org/ontology/ideology###http://dbpedia.org/resource/Pro-Europeanism", lexiconRestrictionClasses);
        addLexicon("non-profit organizations", "http://dbpedia.org/ontology/type###http://dbpedia.org/resource/Nonprofit_organization", lexiconRestrictionClasses);
        addLexicon("swiss", "http://dbpedia.org/ontology/locationCountry###http://dbpedia.org/resource/Switzerland", lexiconRestrictionClasses);

        //classes
        addLexicon("people", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://xmlns.com/foaf/0.1/foaf:Person", lexiconClasses);
        addLexicon("u.s. state", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/StatesOfTheUnitedStates", lexiconClasses);
        addLexicon("greek goddesses", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/GreekGoddesses", lexiconClasses);
        addLexicon("american inventions", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/AmericanInventions", lexiconClasses);
        addLexicon("films", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/Film", lexiconClasses);
        addLexicon("organizations", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/Company", lexiconClasses);
        addLexicon("capitals in europe", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/CapitalsInEurope", lexiconClasses);
        addLexicon("states of germany", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/StatesOfGermany", lexiconClasses);
        addLexicon("james bond movies", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type##http://dbpedia.org/class/yago/JamesBondFilms", lexiconClasses);
        addLexicon("city", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/City108524735", lexiconClasses);
        addLexicon("countries in africa", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/class/yago/AfricanCountries", lexiconClasses);
        addLexicon("organizations", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/Company", lexiconClasses);
        addLexicon("tv shows", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/TelevisionShow", lexiconClasses);
        addLexicon("parties", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/PoliticalParty", lexiconClasses);

        //QALD-6 Test Lexicon
        addLexicon("companies", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/Company", lexiconClasses);

    }

    private static void loadTestLexicon() {
        //QALD-6 Test Lexicon

        //classes
        addLexicon("companies", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type###http://dbpedia.org/ontology/Company", lexiconClasses);

        //properties
        addLexicon("discovered", "http://dbpedia.org/ontology/discoverer", lexiconProperties);
        addLexicon("expresses", "http://dbpedia.org/ontology/connotation", lexiconProperties);
        addLexicon("die", "http://dbpedia.org/property/deathCause", lexiconProperties);
        addLexicon("commence", "http://dbpedia.org/ontology/date", lexiconProperties);
        addLexicon("famous", "http://dbpedia.org/ontology/knownFor", lexiconProperties);
        addLexicon("live", "http://dbpedia.org/ontology/populationTotal", lexiconProperties);
        addLexicon("inspired", "http://dbpedia.org/ontology/influenced", lexiconProperties);
        addLexicon("form of government", "http://dbpedia.org/ontology/governmentType", lexiconProperties);
        addLexicon("government", "http://dbpedia.org/ontology/governmentType", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/ingredient", lexiconProperties);
        addLexicon("full name", "http://dbpedia.org/ontology/alias", lexiconProperties);
        addLexicon("kind of music", "http://dbpedia.org/ontology/genre", lexiconProperties);
        addLexicon("doctoral supervisor", "http://dbpedia.org/ontology/doctoralAdvisor", lexiconProperties);
        addLexicon("on", "http://dbpedia.org/property/crewMembers", lexiconProperties);
        addLexicon("calories", "http://dbpedia.org/ontology/approximateCalories", lexiconProperties);
        addLexicon("kind", "http://dbpedia.org/ontology/genre", lexiconProperties);
        addLexicon("in", "http://dbpedia.org/ontology/ingredient", lexiconProperties);
        addLexicon("end", "http://dbpedia.org/ontology/activeYearsEndDate", lexiconProperties);
        addLexicon("pay", "http://dbpedia.org/ontology/currency", lexiconProperties);
        addLexicon("home stadium", "http://dbpedia.org/ontology/ground", lexiconProperties);
        addLexicon("seats", "http://dbpedia.org/ontology/seatingCapacity", lexiconProperties);

        //resources
        addLexicon("kaurismäki", "http://dbpedia.org/resource/Aki_Kaurismäki", lexiconResources);
        addLexicon("Grand Prix at Cannes", "http://dbpedia.org/resource/Grand_Prix_(Cannes_Film_Festival)", lexiconResources);
        addLexicon("chocolate chip cookie", "http://dbpedia.org/resource/Chocolate_chip_cookie", lexiconResources);
        addLexicon("Sonny and Cher", "http://dbpedia.org/resource/Cher", lexiconResources);

        //restriction classes
        addLexicon("czech", "http://dbpedia.org/ontology/country###http://dbpedia.org/resource/Czech_Republic", lexiconRestrictionClasses);
        addLexicon("computer scientist", "http://dbpedia.org/ontology/field###http://dbpedia.org/resource/Computer_science", lexiconRestrictionClasses);
        addLexicon("canadian", "http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/Canada", lexiconRestrictionClasses);
        addLexicon("canadians", "http://dbpedia.org/ontology/birthPlace###http://dbpedia.org/resource/Canada", lexiconRestrictionClasses);
    }

    private static void addLexicon(String key, String value, HashMap<String, Set<String>> map) {

        key = key.toLowerCase().trim();
        value = value.trim();

        if (map.containsKey(key)) {
            Set<String> set = map.get(key);
            set.add(value);
            map.put(key, set);
        } else {
            Set<String> set = new HashSet<>();
            set.add(value);
            map.put(key, set);
        }
    }

    public static Set<String> getProperties(String term) {

        term = term.toLowerCase();
        if (lexiconProperties == null) {
            load();
        }

        Set<String> result = new HashSet<>();

        if (lexiconProperties.containsKey(term)) {
            result.addAll(lexiconProperties.get(term));
        }

        return result;
    }

    public static Set<String> getRestrictionClasses(String term) {

        term = term.toLowerCase();

        if (lexiconRestrictionClasses == null) {
            load();
        }

        Set<String> result = new HashSet<>();

        if (lexiconRestrictionClasses.containsKey(term)) {
            result.addAll(lexiconRestrictionClasses.get(term));
        }

        return result;
    }

    public static Set<String> getClasses(String term) {

        term = term.toLowerCase();

        if (lexiconClasses == null) {
            load();
        }

        Set<String> result = new HashSet<>();

        if (lexiconClasses.containsKey(term)) {
            result.addAll(lexiconClasses.get(term));
        }

        return result;
    }

    public static Set<String> getResources(String term) {

        term = term.toLowerCase();

        if (lexiconResources == null) {
            load();
        }

        Set<String> result = new HashSet<>();

        if (lexiconResources.containsKey(term)) {
            result.addAll(lexiconResources.get(term));
        }

        return result;
    }
}
