package com.example.testxlsx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

//    Workbook workbook;
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://downloader.disk.yandex.ru/disk/ac8ce195fc4462d0472bb66fc7a79e5031cf2fab83aaa7539a163b906194651b/5fe476a6/fKqInKw3d7bLFOeFnMGnhDpyMsBhQxmvlYfT798WBZDINd7Z5xrmyEtc1efYX9H52hQQnX9XGakhiWB31MMP3FS5VUqjd9RiafJalBOJ-gqr8npumZHI4midPdWhecNq?uid=1130000036490086&filename=5-11%20%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D1%8B.xlsx&disposition=attachment&hash=&limit=0&content_type=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet&owner_uid=1130000036490086&fsize=34147&hid=c17de10314418414867254ddba282291&media_type=document&tknv=v2&etag=5d44d1d29f4a606cd4f147619ccc8d83";
//        String url = "https://bikashthapa01.github.io/excel-reader-android-app/story.xls";
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(MainActivity.this, "Error in Downloading Excel File", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", throwable.getMessage());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {

                if(file != null) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                        XSSFSheet mySheet = myWorkBook.getSheetAt(1);
                        Iterator<Row> rowIterator = mySheet.iterator();
                        String content;
                        while (rowIterator.hasNext()) {
                            Row row = rowIterator.next();

                            Iterator<Cell> cellIterator = row.cellIterator();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                switch (cell.getCellType()){
                                    case Cell.CELL_TYPE_STRING:
                                        content = cell.getStringCellValue();
                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        content = String.valueOf(cell.getNumericCellValue());
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }
}