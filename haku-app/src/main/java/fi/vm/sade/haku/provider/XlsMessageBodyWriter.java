package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
        String sheetname = xlsModel.asId + "_" + xlsModel.hakukausiVuosi + "_" + xlsModel.ao.getName();
        Sheet sheet = wb.createSheet(sheetname);
        sheet.setDefaultColumnWidth(20);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setQuotePrefixed(true);

        createRow(sheet, cellStyle, "Haku", xlsModel.asName);
        createRow(sheet, cellStyle, "Haku oid", xlsModel.asId);
        createRow(sheet, cellStyle, "Hakukausi", xlsModel.getHakukausi(koodistoService.getHakukausi()));
        createRow(sheet, cellStyle, "Hakuvuosi", xlsModel.hakukausiVuosi);
        createRow(sheet, cellStyle, "Hakukohde", xlsModel.ao.getName());

        createRow(sheet, cellStyle);

        Row titleRow = createRow(sheet, cellStyle);
        for (Element title : xlsModel.columnKeyList()) {
            createCell(titleRow, cellStyle).setCellValue(xlsModel.getText(title));
        }

        List<String> rowKeys = xlsModel.rowKeyList();
        List<Element> colKeys = xlsModel.columnKeyList();
        int rows = rowKeys.size();
        int cols = colKeys.size();
        int rowOffset = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rows; i++) {
            Row row = sheet.createRow(rowOffset + i);
            for (int j = 0; j < cols; j++) {
                Cell cell = newCell(row, cellStyle, j);
                cell.setCellValue(xlsModel.getValue(rowKeys.get(i), colKeys.get(j)));
            }
        }
        for (int i = 0; i < colKeys.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        httpHeaders.add("content-disposition", "attachment; filename=" + URLEncoder.encode(sheetname, "UTF-8") + ".xls");
        wb.write(entityStream);
    }

    private Row createRow(final Sheet sheet, CellStyle cellStyle, String... values) {
        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(lastRowNum + 1);
        for (String value : values) {
            createCell(row, cellStyle).setCellValue(value);
        }
        return row;
    }

    private Cell createCell(final Row row, CellStyle cellStyle) {
        short lastCellNum = row.getLastCellNum();
        return newCell(row, cellStyle, lastCellNum == -1 ? 0 : lastCellNum);
    }

    private Cell newCell(Row row, CellStyle cellStyle, int j) {
        Cell cell = row.createCell(j);
        cell.setCellStyle(cellStyle);
        return cell;
    }
}
