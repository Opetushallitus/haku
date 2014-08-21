package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.resource.XlsParameter;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

@Service
@Produces("application/octet-stream")
@Provider
public class XlsWriter implements MessageBodyWriter<XlsParameter> {


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return XlsParameter.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(XlsParameter xlsParameter, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(XlsParameter xlsParameter, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        String lang = "fi";

        I18nText name = xlsParameter.getApplicationSystem().getName();
        Integer hakukausiVuosi = xlsParameter.getApplicationSystem().getHakukausiVuosi();

        Workbook wb = new HSSFWorkbook();
        String haunNimi = xlsParameter.getApplicationSystem().getName().getTranslations().get(lang);
        Sheet sheet = wb.createSheet(haunNimi + " " + hakukausiVuosi + " " + xlsParameter.getHakukohteenNimi());
        CreationHelper createHelper = wb.getCreationHelper();
        // Create a row and put some cells in it. Rows are 0 based.
        sheet.setDefaultColumnWidth(20);


        int currentRow = 0;
        int currentColumn = 0;

        Map<String,Question> questions = xlsParameter.getElementsByType();
        Row title = sheet.createRow(currentRow);
        ArrayList questionIndexes = new ArrayList(questions.size());
        for (Question titled : questions.values()) {
            Cell titleCell = title.createCell(currentColumn);
            if (titled.getI18nText() != null) {
                titleCell.setCellValue(titled.getI18nText().getTranslations().get("fi"));
                sheet.autoSizeColumn(currentColumn);
                currentColumn++;
                questionIndexes.add(titled.getId());
            }
        }
        currentRow++;

        List<Map<String, Object>> applications = xlsParameter.getApplications();

        for (Map<String, Object> application : applications) {

            title = sheet.createRow(currentRow);
            Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
            for (Map.Entry<String, Object> vaiheEntry : vastaukset.entrySet()) {
                Map<String, String> vaihe = (Map<String, String>) vaiheEntry.getValue();
                for (Map.Entry<String, String> vastaus : vaihe.entrySet()) {
                    int column = questionIndexes.indexOf(vastaus.getKey());
                    System.out.println(vastaus.getKey() + " = " + vastaus.getValue() + "|" + column);
                    if (column > -1) {
                        Cell kentta = title.createCell(column);
                        Titled titled = questions.get(vastaus.getKey());
                        if (titled != null && titled.getI18nText() != null && titled.getI18nText().getTranslations() != null && titled.getI18nText().getTranslations().get("fi") != null) {
                            kentta.setCellValue(vastaus.getValue());
                        }
                    }
                }
            }
        }
        httpHeaders.add("content-disposition", "attachment; filename=raportti.xls");
        wb.write(entityStream);
    }
}
