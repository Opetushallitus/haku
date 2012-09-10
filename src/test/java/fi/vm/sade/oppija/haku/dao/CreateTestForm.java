package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.builders.ApplicationPeriodBuilder;
import fi.vm.sade.oppija.haku.domain.builders.ElementBuilder;
import fi.vm.sade.oppija.haku.domain.builders.FormBuilder;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author jukka
 * @version 9/7/121:02 PM}
 * @since 1.1
 */
public class CreateTestForm {

    @Test
    public void test() throws Exception {
        final Category cat1 = new Category("cat1", "cat1");
        final Question question = new TextQuestion("id", "id", "id");
        cat1.addChild(question);
        final Element element = new ElementBuilder(cat1).build();
        final Form form = new FormBuilder("1", "1").withChild(element).build();
        final ApplicationPeriod applicationPeriod = new ApplicationPeriodBuilder("" + System.currentTimeMillis()).withForm(form).build();
        final FormModel model = new FormModelBuilder().withApplicationPeriods(applicationPeriod).build();

        ObjectMapper mapper = new ObjectMapper();
        final StringWriter w = new StringWriter();
        mapper.defaultPrettyPrintingWriter().writeValue(w, model);
        System.out.println(w.toString());

    }


    @Test
    public void testDeserialize() throws IOException {
        String foo = "{\n" +
                "  \"applicationPerioidMap\" : {\n" +
                "    \"1347021371879\" : {\n" +
                "      \"id\" : \"1347021371879\",\n" +
                "      \"active\" : false,\n" +
                "      \"forms\" : {\n" +
                "        \"1\" : {\n" +
                "          \"title\" : \"\",\n" +
                "          \"id\" : \"1\",\n" +
                "          \"type\" : \"Form\",\n" +
                "          \"attributes\" : [ ],\n" +
                "          \"children\" : [ {\n" +
                "            \"title\" : \"\",\n" +
                "            \"id\" : \"cat1\",\n" +
                "            \"type\" : \"Category\",\n" +
                "            \"attributes\" : [ ],\n" +
                "            \"children\" : [ {\n" +
                "              \"title\" : \"\",\n" +
                "              \"id\" : \"id\",\n" +
                "              \"type\" : \"TextQuestion\",\n" +
                "              \"attributes\" : [ ],\n" +
                "              \"children\" : [ ],\n" +
                "              \"help\" : \"\"\n" +
                "            } ],\n" +
                "            \"help\" : \"\"\n" +
                "          } ],\n" +
                "          \"help\" : \"\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        final FormModel formModel = mapper.readValue(foo, FormModel.class);
        System.out.println("joo");
    }

}
