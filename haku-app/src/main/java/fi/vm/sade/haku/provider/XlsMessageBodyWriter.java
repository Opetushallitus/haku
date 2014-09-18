package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.resource.XlsParameter;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Produces("application/vnd.ms-excel")
@Provider
public class XlsMessageBodyWriter implements MessageBodyWriter<XlsParameter> {

    private static final short EMPTY_COLUMN_WIDTH = 10;

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

        String lang = "fi";
        ApplicationSystem applicationSystem = xlsParameter.getApplicationSystem();
        Integer hakukausiVuosi = applicationSystem.getHakukausiVuosi();
        String hakukausi = null;
        for (Option option : koodistoService.getHakukausi()) {
            if (option.getValue().equals(applicationSystem.getHakukausiUri())) {
                hakukausi = option.getI18nText().getTranslations().get(lang);
            }
        }
        Workbook wb = new HSSFWorkbook();
        String haunNimi = applicationSystem.getName().getTranslations().get(lang);
        String raportinNimi = xlsParameter.getAsid() + "_" + hakukausiVuosi + "_" + xlsParameter.getAoid();
        Sheet sheet = wb.createSheet(raportinNimi);
        CreationHelper createHelper = wb.getCreationHelper();
        // Create a row and put some cells in it. Rows are 0 based.
        sheet.setDefaultColumnWidth(20);

        int currentRowIndex = 0;
        int currentColumnIndex = 0;

        createKeyValueRow(sheet, currentRowIndex++, "Haku", haunNimi);
        createKeyValueRow(sheet, currentRowIndex++, "Haku oid", xlsParameter.getAsid());
        createKeyValueRow(sheet, currentRowIndex++, "Hakukausi", (hakukausi + " " + applicationSystem.getHakukausiVuosi().toString()).trim());
        createKeyValueRow(sheet, currentRowIndex++, "Hakukohde", xlsParameter.getAoid());

        sheet.createRow(currentRowIndex++);

        Map<String, Question> questions = xlsParameter.getQuestions();
        Row titleRow = sheet.createRow(currentRowIndex);
        ArrayList questionIndexes = new ArrayList(questions.size());
        for (Question titled : questions.values()) {
            sheet.setColumnWidth(currentColumnIndex,EMPTY_COLUMN_WIDTH);
            Cell titleCell = titleRow.createCell(currentColumnIndex);
            if (titled.getI18nText() != null) {
                titleCell.setCellValue(ElementUtil.getText(titled, lang));
                questionIndexes.add(titled.getId());
                currentColumnIndex++;
            }
        }

        List<Map<String, Object>> applications = xlsParameter.getApplications();
        currentRowIndex++;
        for (Map<String, Object> application : applications) {
            Row currentRow = sheet.createRow(currentRowIndex++);
            Map<String, Object> vastaukset = (Map<String, Object>) application.get("answers");
            for (Map.Entry<String, Object> vastauksetVaiheittain : vastaukset.entrySet()) {
                Map<String, String> vaiheenVastaukset = (Map<String, String>) vastauksetVaiheittain.getValue();
                for (Map.Entry<String, String> vastaus : vaiheenVastaukset.entrySet()) {
                    int column = questionIndexes.indexOf(vastaus.getKey());
                    if (column > -1) {
                        Cell kentta = currentRow.createCell(column);
                        Question question = questions.get(vastaus.getKey());
                        String title = ElementUtil.getText(question, lang);
                        if (title != null) {
                            if (question instanceof OptionQuestion) {
                                Option option = ((OptionQuestion) question).getData().get(vastaus.getValue());
                                if (option != null) {
                                    sheet.autoSizeColumn(column);
                                    kentta.setCellValue(ElementUtil.getText(option, lang));
                                }
                            } else {
                                sheet.autoSizeColumn(column);
                                kentta.setCellValue(vastaus.getValue());
                            }
                        }
                    }
                }
            }
        }
        httpHeaders.add("content-disposition", "attachment; filename=" + raportinNimi + ".xls");
        wb.write(entityStream);
    }

    private void createKeyValueRow(final Sheet sheet, int row, String... values) {
        Row infoRow = sheet.createRow(row);
        for (int i = 0; i < values.length; i++) {
            infoRow.createCell(i).setCellValue(values[i]);
        }
    }
}
