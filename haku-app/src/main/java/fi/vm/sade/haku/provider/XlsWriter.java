package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.resource.XLSParameter;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

@Service
@Produces("application/xls")
@Provider
public class XlsWriter implements MessageBodyWriter<XLSParameter> {


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return XLSParameter.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(XLSParameter xlsParameter, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeTo(XLSParameter xlsParameter, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Laskentataulukko");
        CreationHelper createHelper = wb.getCreationHelper();
        // Create a row and put some cells in it. Rows are 0 based.
        int i = 0;
        int j = 0;
        for (Application application : xlsParameter.getApplications()) {

            Row title = sheet.createRow(i);
            Row value = sheet.createRow(i + 1);
            Map<String, String> vastauksetMerged = application.getVastauksetMerged();

            for (Map.Entry<String, String> vastaus : vastauksetMerged.entrySet()) {
                Cell titleCell = title.createCell(j);
                Cell valueCell = value.createCell(j);
                if (StringUtils.isNotEmpty(vastaus.getValue())) {
                    Titled titled = xlsParameter.getElementsByType().get(vastaus.getKey());
                    if (titled != null && titled.getI18nText() != null && titled.getI18nText().getTranslations() != null && titled.getI18nText().getTranslations().get("fi") != null) {
                        String fi = titled.getI18nText().getTranslations().get("fi");
                        titleCell.setCellValue(createHelper.createRichTextString(fi));
                        valueCell.setCellValue(createHelper.createRichTextString(vastaus.getValue()));
                        j++;
                    }
                }
            }
            i++;
        }
        wb.write(entityStream);
    }
}
