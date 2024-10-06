package dataProvider;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataProviderUtils {
    @DataProvider(parallel = true,name = "DataProvider")
    public Object[][] dataProvider_web(Method m){
        String fileName = System.getProperty("user.dir")+"/src/test/resources/dataSheet/BookingDataSheet.xlsx";
        String sheetName = "Data";
        Object[][] data = getExcelData(fileName,sheetName,m.getName());
        return data;
    }
    public Object[][] getExcelData(String fileName,String sheetName,String methodName) {
        Object data[][] = null;
        List<List<Object>> dataList = new ArrayList<>();
        try{
            FileInputStream fs = new FileInputStream(fileName);
            XSSFWorkbook wb = new XSSFWorkbook(fs);
            XSSFSheet sheet = wb.getSheet(sheetName);
            XSSFRow row = sheet.getRow(0);
            Cell cell = null;
            for(int i=0;i<=sheet.getLastRowNum();i++){
                row = sheet.getRow(i);
                List<Object> singleData = new ArrayList<>();
                if(row.getCell(0).getStringCellValue().equals(methodName)){
                    XSSFRow finalRow = row;
                    singleData = IntStream.range(1,row.getLastCellNum()).asLongStream().mapToObj(j-> finalRow.getCell((int)j).getStringCellValue()).collect(Collectors.toList());
                    dataList.add(singleData);
                }
            }
            data = dataList.stream().map(item->item.toArray(new Object[0])).toArray(Object[][]::new);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
