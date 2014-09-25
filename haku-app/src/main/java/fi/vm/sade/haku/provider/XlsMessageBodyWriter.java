package fi.vm.sade.haku.provider;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
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
import java.util.List;

@Service
@Produces("application/vnd.ms-excel")
@Provider
public class XlsMessageBodyWriter implements MessageBodyWriter<XlsModel> {

    private static final short EMPTY_COLUMN_WIDTH = 10;
    private static final String LANG = "fi";

    private final KoodistoService koodistoService;

    @Autowired
    public XlsMessageBodyWriter(final KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return XlsModel.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(XlsModel xlsParameter, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(XlsModel xlsModel, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        Workbook wb = new HSSFWorkbook();
        String raportinNimi = xlsModel.asId + "_" + xlsModel.hakukausiVuosi + "_" + xlsModel.aoName;
        Sheet sheet = wb.createSheet(raportinNimi);
        // Create a row and put some cells in it. Rows are 0 based.
        sheet.setDefaultColumnWidth(20);

        int currentRowIndex = 0;
        int currentColumnIndex = 0;

        createKeyValueRow(sheet, currentRowIndex++, "Haku", xlsModel.asName);
        createKeyValueRow(sheet, currentRowIndex++, "Haku oid", xlsModel.asId);
        createKeyValueRow(sheet, currentRowIndex++, "Hakukausi", xlsModel.getHakukausi(koodistoService.getHakukausi()));
        createKeyValueRow(sheet, currentRowIndex++, "Hakuvuosi", xlsModel.hakukausiVuosi);
        createKeyValueRow(sheet, currentRowIndex++, "Hakukohde", xlsModel.aoName);

        sheet.createRow(currentRowIndex++);

        ArrayTable<String, Element, Object> table = xlsModel.getTable();

        Row titleRow = sheet.createRow(currentRowIndex++);

        List<Element> elements = xlsModel.columnKeyList();

        for (Element title : elements) {
            if (xlsModel.isQuestionAnswered(title)) {
                Cell titleCell = titleRow.createCell(currentColumnIndex);
                titleCell.setCellValue(xlsModel.getText(title));
                currentColumnIndex++;
            }
        }
        currentColumnIndex++;

        ImmutableList<String> rowKeys = table.rowKeyList();
        Iterable<Element> colKeys = xlsModel.columnKeyList();
        for (String rowKey : rowKeys) {
            Row row = sheet.createRow(currentRowIndex);
            int collIndex = 0;
            for (Element colKey : colKeys) {
                Cell cell = row.createCell(collIndex);
                cell.setCellValue(xlsModel.getValue(rowKey, colKey));
                sheet.autoSizeColumn(collIndex);
                collIndex++;
            }
            currentRowIndex++;
        }
        httpHeaders.add("content-disposition", "attachment; filename=" + URLEncoder.encode(raportinNimi, "UTF-8") + ".xls");
        wb.write(entityStream);
    }



    private void createKeyValueRow(final Sheet sheet, int row, String... values) {
        Row infoRow = sheet.createRow(row);
        for (int i = 0; i < values.length; i++) {
            infoRow.createCell(i).setCellValue(values[i]);
        }
    }
}
