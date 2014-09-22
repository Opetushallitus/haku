package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.resource.XlsParameter;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Produces("application/vnd.ms-excel")
@Provider
public class XlsMessageBodyWriter implements MessageBodyWriter<XlsParameter> {

    private static final short EMPTY_COLUMN_WIDTH = 10;
    private static final String LANG = "fi";

    private final KoodistoService koodistoService;

    @Autowired
    public XlsMessageBodyWriter(final KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

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

        ApplicationSystem applicationSystem = xlsParameter.getApplicationSystem();
        String hakukausi = null;
        for (Option option : koodistoService.getHakukausi()) {
            if (option.getValue().equals(applicationSystem.getHakukausiUri())) {
                hakukausi = ElementUtil.getText(option, LANG);
            }
        }
        Workbook wb = new HSSFWorkbook();
        String haunNimi = applicationSystem.getName().getTranslations().get(LANG);
        Integer hakukausiVuosi = applicationSystem.getHakukausiVuosi();
        String raportinNimi = xlsParameter.getAsid() + "_" + hakukausiVuosi + "_" + xlsParameter.getAoid();
        Sheet sheet = wb.createSheet(raportinNimi);
        // Create a row and put some cells in it. Rows are 0 based.
        sheet.setDefaultColumnWidth(20);

        int currentRowIndex = 0;
        int currentColumnIndex = 0;

        createKeyValueRow(sheet, currentRowIndex++, "Haku", haunNimi);
        createKeyValueRow(sheet, currentRowIndex++, "Haku oid", xlsParameter.getAsid());
        createKeyValueRow(sheet, currentRowIndex++, "Hakukausi", hakukausi);
        createKeyValueRow(sheet, currentRowIndex++, "Hakuvuosi", applicationSystem.getHakukausiVuosi().toString());
        createKeyValueRow(sheet, currentRowIndex++, "Hakukohde", xlsParameter.getAoid());

        sheet.createRow(currentRowIndex++);

        List<Element> questions = xlsParameter.getQuestions();
        Row titleRow = sheet.createRow(currentRowIndex);
        ArrayList questionIndexes = new ArrayList(questions.size());
        Map<String, Element> elementMap = new HashMap<String, Element>();
        for (Element titled : questions) {
            sheet.setColumnWidth(currentColumnIndex, EMPTY_COLUMN_WIDTH);
            Cell titleCell = titleRow.createCell(currentColumnIndex);
            if (((Titled) titled).getI18nText() != null) {
                titleCell.setCellValue(ElementUtil.getText(titled, LANG));
                questionIndexes.add(titled.getId());
                currentColumnIndex++;
                elementMap.put(titled.getId(), titled);
            }
        }


        List<Map<String, Object>> applications = xlsParameter.getApplications();
        currentRowIndex++;
        for (Map<String, Object> application : applications) {
            currentRowIndex = fillRow(LANG, sheet, elementMap, questionIndexes, application, currentRowIndex);
        }
        httpHeaders.add("content-disposition", "attachment; filename=" + URLEncoder.encode(raportinNimi, "UTF-8") + ".xls");
        wb.write(entityStream);
    }

    private int fillRow(String lang, Sheet sheet, Map<String, Element> questions, ArrayList questionIndexes, Map<String, Object> application, int currentRowIndex) {
        Row currentRow = sheet.createRow(currentRowIndex);
        currentRowIndex++;
        Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
        for (Map.Entry<String, Object> vastauksetVaiheittain : vastaukset.entrySet()) {
            Map<String, String> vaiheenVastaukset = (Map<String, String>) vastauksetVaiheittain.getValue();
            for (Map.Entry<String, String> vastaus : vaiheenVastaukset.entrySet()) {
                setCellValue(lang, sheet, questions, questionIndexes, currentRow, vastaus);
            }
        }
        return currentRowIndex;
    }

    private void setCellValue(String lang, Sheet sheet, Map<String, Element> questions, ArrayList questionIndexes, Row currentRow, Map.Entry<String, String> vastaus) {
        int column = questionIndexes.indexOf(vastaus.getKey());
        if (column > -1) {
            Cell kentta = currentRow.createCell(column);
            Element question = questions.get(vastaus.getKey());
            String title = ElementUtil.getText(question, lang);
            if (title != null) {
                sheet.autoSizeColumn(column);
                kentta.setCellValue(getElementValue(lang, vastaus, question));
            }
        }
    }

    private String getElementValue(String lang, Map.Entry<String, String> vastaus, Element question) {
        String value = null;
        if (question instanceof OptionQuestion) {
            Option option = ((OptionQuestion) question).getData().get(vastaus.getValue());
            if (option != null) {
                value = ElementUtil.getText(option, lang);
            }
        } else if (question instanceof CheckBox) {
            if (Boolean.TRUE.toString().equals(vastaus.getValue())) {
                value = "Kyllä";
            } else {
                value = "Ei";
            }
        } else {
            value = vastaus.getValue();
        }
        return value;
    }

    private void createKeyValueRow(final Sheet sheet, int row, String... values) {
        Row infoRow = sheet.createRow(row);
        for (int i = 0; i < values.length; i++) {
            infoRow.createCell(i).setCellValue(values[i]);
        }
    }
}
