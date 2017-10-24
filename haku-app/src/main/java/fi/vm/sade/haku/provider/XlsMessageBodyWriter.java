package fi.vm.sade.haku.provider;

import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.javautils.poi.OphCellStyles.OphHssfCellStyles;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

        HSSFWorkbook wb = new HSSFWorkbook();
        String sheetname = xlsModel.asId + "_" + xlsModel.hakukausiVuosi + "_" + xlsModel.ao.getName();
        HSSFSheet sheet = wb.createSheet(sheetname);
        sheet.setDefaultColumnWidth(20);

        OphHssfCellStyles cellStyles = new OphHssfCellStyles(wb);

        createRow(sheet, cellStyles, "Haku", xlsModel.asName);
        createRow(sheet, cellStyles, "Haku oid", xlsModel.asId);
        createRow(sheet, cellStyles, "Hakukausi", xlsModel.getHakukausi(koodistoService.getHakukausi()));
        createRow(sheet, cellStyles, "Hakuvuosi", xlsModel.hakukausiVuosi);
        createRow(sheet, cellStyles, "Hakukohde", xlsModel.ao.getName());

        createRow(sheet, cellStyles);

        HSSFRow titleRow = createRow(sheet, cellStyles);
        for (Element title : xlsModel.columnKeyList()) {
            HSSFCell cell = createCell(titleRow);
            cell.setCellValue(xlsModel.getText(title));
            cellStyles.apply(cell);
        }

        List<String> rowKeys = xlsModel.rowKeyList();
        List<Element> colKeys = xlsModel.columnKeyList();
        int rows = rowKeys.size();
        int cols = colKeys.size();
        int rowOffset = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rows; i++) {
            HSSFRow row = sheet.createRow(rowOffset + i);
            for (int j = 0; j < cols; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(xlsModel.getValue(rowKeys.get(i), colKeys.get(j)));
                cellStyles.apply(cell);
            }
        }
        for (int i = 0; i < colKeys.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        httpHeaders.add("content-disposition", "attachment; filename=" + URLEncoder.encode(sheetname, "UTF-8") + ".xls");
        wb.write(entityStream);
    }

    private HSSFRow createRow(final HSSFSheet sheet, OphHssfCellStyles cellStyles, String... values) {
        int lastRowNum = sheet.getLastRowNum();
        HSSFRow row = sheet.createRow(lastRowNum + 1);
        for (String value : values) {
            HSSFCell cell = createCell(row);
            cell.setCellValue(value);
            cellStyles.apply(cell);
        }
        return row;
    }

    private HSSFCell createCell(final HSSFRow row) {
        short lastCellNum = row.getLastCellNum();
        int j = lastCellNum == -1 ? 0 : lastCellNum;
        return row.createCell(j);
    }
}
