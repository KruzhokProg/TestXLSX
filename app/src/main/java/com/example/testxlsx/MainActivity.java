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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

//    Workbook workbook;
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://downloader.disk.yandex.ru/disk/f4b824035b28b045e7d995fa55a792410d7a3de021d167e5e425c900a1e18431/5fe610ce/fKqInKw3d7bLFOeFnMGnhDpyMsBhQxmvlYfT798WBZDINd7Z5xrmyEtc1efYX9H52hQQnX9XGakhiWB31MMP3FS5VUqjd9RiafJalBOJ-gqr8npumZHI4midPdWhecNq?uid=1130000036490086&filename=5-11%20%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D1%8B.xlsx&disposition=attachment&hash=&limit=0&content_type=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet&owner_uid=1130000036490086&fsize=34147&hid=c17de10314418414867254ddba282291&media_type=document&tknv=v2&etag=5d44d1d29f4a606cd4f147619ccc8d83";
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

//                if(file != null) {
//                    FileInputStream fis = null;
//                    Boolean isSchedulePassed = false;
//
//                    try {
//                        fis = new FileInputStream(file);
//                        String content;
//                        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
//                        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
//                        Iterator<Row> rowIterator = mySheet.iterator();
//
//                        while (rowIterator.hasNext()) {
//                            Row row = rowIterator.next();
//                            Iterator<Cell> cellIterator = row.cellIterator();
//
//                            while (cellIterator.hasNext()) {
//                                Cell cell = cellIterator.next();
//                                switch (cell.getCellType()){
//                                    case Cell.CELL_TYPE_STRING:
//                                        content = cell.getStringCellValue();
//                                        content = content.trim();
//                                        if(content.contains("Расписание")){
//                                            isSchedulePassed = true;
//                                        }
//                                        else if(isSchedulePassed == true){
//
//                                        }
//                                        break;
//                                    case Cell.CELL_TYPE_NUMERIC:
//                                        content = String.valueOf(cell.getNumericCellValue());
//                                }
//                            }
//                        }
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
                //---------------------------------------

                if(file != null) {
                    try {

                        List<Schedule> schedules = new ArrayList<>();
                        Boolean isSchedulePassed = false;
                        Boolean isMonday = false;
                        Boolean isTuesday = false;
                        Boolean isWednesday = false;
                        Boolean isThursday = false;
                        Boolean isFriday = false;
                        Boolean isLessonNum; // начало строки с уроками

                        FileInputStream fis = new FileInputStream(file);
                        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        Iterator<Row> rowIterator = mySheet.iterator();
                        String content;
                        Integer colNum=0; // количество классов в параллели
                        Integer curNum; // текущий класс в параллели
                        Integer emptyLessonNum = 0; // количество пропусков в уроках
                        while (rowIterator.hasNext()) {

                            isLessonNum = false;
                            emptyLessonNum=0;
                            curNum = 0;
                            if(colNum!=0){
                                isSchedulePassed = false;
                            }

                            Row row = rowIterator.next();
                            Iterator<Cell> cellIterator = row.cellIterator();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                switch (cell.getCellType()){
                                    case Cell.CELL_TYPE_STRING:
                                        content = cell.getStringCellValue();
                                        content = content.trim();
                                        if(content.contains("Расписание")){
                                            isSchedulePassed = true;
                                        }
                                        else if(isSchedulePassed == true){
                                            // строка с перечнем классов в параллели
                                            colNum++;
                                            Schedule schedule = new Schedule();
                                            schedule.setGrade(content);
                                            schedules.add(schedule);
                                        }
                                        else{
                                            if(content.equals("Пятница") && isFriday == false){
                                                isFriday = false;
                                                for(int i=0; i<colNum; i++){
                                                    Weekdays fridayLessons = new Weekdays();
                                                    fridayLessons.setWeekday("Пятница");
                                                    schedules.get(i).getWeekdays().add(fridayLessons);
                                                }
                                                isFriday = true;
                                            }
                                            else if(isFriday == true){
                                                schedules.get(curNum).getWeekdays().get(4).getLessons().add(content);
                                                curNum++;
                                            }
                                            else if(content.equals("Четверг") && isThursday == false){
                                                isThursday = false;
                                                for(int i=0; i<colNum; i++){
                                                    Weekdays thursdayLessons = new Weekdays();
                                                    thursdayLessons.setWeekday("Четверг");
                                                    schedules.get(i).getWeekdays().add(thursdayLessons);
                                                }
                                                isThursday = true;
                                            }
                                            else if(isThursday == true){
                                                schedules.get(curNum).getWeekdays().get(3).getLessons().add(content);
                                                curNum++;
                                            }
                                            else if(content.equals("Среда") && isWednesday == false){
                                                isWednesday = false;
                                                for(int i=0; i<colNum; i++){
                                                    Weekdays wednsdayLessons = new Weekdays();
                                                    wednsdayLessons.setWeekday("Среда");
                                                    schedules.get(i).getWeekdays().add(wednsdayLessons);
                                                }
                                                isWednesday = true;
                                            }
                                            else if(isWednesday == true){
                                                schedules.get(curNum).getWeekdays().get(2).getLessons().add(content);
                                                curNum++;
                                            }
                                            else if(content.equals("Вторник") && isTuesday == false){
                                                isTuesday = false;
                                                for(int i=0; i<colNum; i++){
                                                    Weekdays tuesdayLessons = new Weekdays();
                                                    tuesdayLessons.setWeekday("Вторник");
                                                    schedules.get(i).getWeekdays().add(tuesdayLessons);
                                                }
                                                isTuesday = true;
                                            }
                                            else if(isTuesday == true){
                                                schedules.get(curNum).getWeekdays().get(1).getLessons().add(content);
                                                curNum++;
                                            }
                                            else if(content.equals("Понедельник") && isMonday == false){
                                                for(int i=0; i<colNum; i++){
                                                    Weekdays mondayLessons = new Weekdays();
                                                    mondayLessons.setWeekday("Понедельник");
                                                    schedules.get(i).getWeekdays().add(mondayLessons);
                                                }
                                                isMonday = true;

                                            }
                                            else if(isMonday == true){

                                                schedules.get(curNum).getWeekdays().get(0).getLessons().add(content);
                                                curNum++;
//                                                if(isFirstLesson) {
//                                                    schedules.get(curNum).getWeekdays().get(0).getLessons().setFirst(content);
//                                                }
                                            }


                                        }

                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        content = String.valueOf(cell.getNumericCellValue());
                                        isLessonNum = true;
                                    case Cell.CELL_TYPE_BLANK:
                                        if(isLessonNum == true){
                                            curNum = emptyLessonNum;
                                            emptyLessonNum++;
                                        }
                                }
                            }
                        }

                        String foo="";
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