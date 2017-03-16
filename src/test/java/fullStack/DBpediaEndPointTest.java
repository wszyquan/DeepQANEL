/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fullStack;

import de.citec.sc.utils.DBpediaEndpoint;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author sherzod
 */
public class DBpediaEndPointTest {
    
    @Test
    public void test(){
        String range = DBpediaEndpoint.getRange("http://dbpedia.org/ontology/birthPlace");
        
        Assert.assertEquals(true, range.equals("http://dbpedia.org/ontology/Place"));
        System.out.println("Range: "+ range);
    }
}
