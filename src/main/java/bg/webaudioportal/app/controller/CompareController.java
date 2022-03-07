package bg.webaudioportal.app.controller;

import bg.webaudioportal.app.model.ResultOfCompare;
import bg.webaudioportal.app.service.CompareService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/report/product")
public class CompareController {

    @RequestMapping("/{fileName:.+}")
    public ResponseEntity<Resource> makeAReport(@PathVariable("fileName") String fileName) throws IOException, InvalidMidiDataException {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Audio Compare");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(1, 10000);


            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setWrapText(true);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Note name");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Result of comparison");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(2);
            headerCell.setCellValue("Start MS");
            headerCell.setCellStyle(headerStyle);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);


            List<ResultOfCompare> resultOfCompares = null;
            if (fileName!=null) {
                resultOfCompares = CompareService.compareMidiFiles(fileName);
            }
            sheet.setAutoFilter(new CellRangeAddress(0, resultOfCompares.size(), 0, 2));
            int rowCount = 1;

            for (ResultOfCompare resultOfCompare : resultOfCompares) {
                Row row1 = sheet.createRow(rowCount++);
                Cell cell = row1.createCell(0);
                cell.setCellValue(resultOfCompare.getNoteName());
                cell.setCellStyle(style);

                cell = row1.createCell(1);
                cell.setCellValue(resultOfCompare.getResultOfComparison());
                cell.setCellStyle(style);

                cell = row1.createCell(2);
                cell.setCellValue(resultOfCompare.getStartMS());
                cell.setCellStyle(style);
            }


            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            workbook.close();

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductExcelReport.xlsx");

            ResponseEntity<Resource> response = new ResponseEntity<Resource>(new InputStreamResource(is), headers,
                    HttpStatus.OK);

        return response;
    }
}
