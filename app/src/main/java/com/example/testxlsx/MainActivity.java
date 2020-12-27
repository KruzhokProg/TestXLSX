package com.example.testxlsx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

//    Workbook workbook;
    AsyncHttpClient asyncHttpClient;
    NiceSpinner spCorpus, spGrade, spLetter, spWeekday;
    RecyclerView rvSchedule;
    List<Schedule> schedules;
    ScheduleAdapter adapter;
    List<LessonInfo> out;
//    ArrayAdapter<String> gradeAdapter, letterAdapter, corpusAdapter, weekDayAdapter;
//    SpinnerAdapter gradeAdapter, letterAdapter, corpusAdapter, weekDayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spCorpus = findViewById(R.id.spCorpus);
        spGrade = findViewById(R.id.spGrade);
        spLetter = findViewById(R.id.spLetter);
        spWeekday = findViewById(R.id.spWeekday);
        rvSchedule = findViewById(R.id.rvSchedule);
        adapter = new ScheduleAdapter(this);
        out = new ArrayList<>();
        adapter.setLessons(out);
        rvSchedule.setAdapter(adapter);

        String url = "https://downloader.disk.yandex.ru/disk/6f9bc1f42c6e5e543c7b3b2bb771ee30996e56ceedee3feebd3f9c7cab80ef0a/5fe8c159/fKqInKw3d7bLFOeFnMGnhDSlwWRoHXW7Q6ezZf7I5GZFueQCJIdQ5u9xvzNXIcBIM_YhO-d18LZPE1IFkYfWQDhVfmPxGKVrrRtZQouiHcur8npumZHI4midPdWhecNq?uid=1130000036490086&filename=5-11%20%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D1%8B.xlsx&disposition=attachment&hash=&limit=0&content_type=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet&owner_uid=1130000036490086&fsize=34691&hid=7c5db45c15eabfa34d3c62a66a47d8eb&media_type=document&tknv=v2&etag=dabcfec1adfd37ae3808a23316821bb9";
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

                        schedules = new ArrayList<>();
                        Boolean isSchedulePassed, isMonday, isTuesday, isWednesday, isThursday, isFriday;
                        Boolean isLessonNum; // начало строки с уроками
                        Integer colNum; // количество классов в параллели
                        Integer curNum; // текущий класс в параллели
                        Integer c1=0,c2=0;
                        String[] lessonRoom;
                        String lesson="", room="";

                        FileInputStream fis = new FileInputStream(file);
                        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                        Integer numOfSheets = myWorkBook.getNumberOfSheets();
                        for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++) {

                            isSchedulePassed = false;
                            isMonday = false;
                            isTuesday = false;
                            isWednesday = false;
                            isThursday = false;
                            isFriday = false;

                            XSSFSheet mySheet = myWorkBook.getSheetAt(sheetNum);
                            Iterator<Row> rowIterator = mySheet.iterator();
                            String content;
                            colNum = 0; // количество классов в параллели

                            Integer emptyLessonNum = 0; // количество пропусков в уроках
                            while (rowIterator.hasNext()) {

                                isLessonNum = false;
                                curNum = c1;
                                emptyLessonNum = 0;
                                if (colNum != 0) {
                                    isSchedulePassed = false;
                                }

                                Row row = rowIterator.next();
                                Iterator<Cell> cellIterator = row.cellIterator();
                                while (cellIterator.hasNext()) {
                                    Cell cell = cellIterator.next();
                                    switch (cell.getCellType()) {
                                        case Cell.CELL_TYPE_STRING:
                                            content = cell.getStringCellValue();
                                            content = content.trim().replace("-","");
                                            lessonRoom = content.split(" ");
                                            lessonRoom = Arrays.stream(lessonRoom).filter(x -> !x.isEmpty()).toArray(String[]::new);

                                            if(lessonRoom.length == 0){
                                                lesson = "----------------------";
                                                room = "";
                                            }
                                            else if(content.contains("физическая культура")){
                                                lesson = "физическая культура";
                                                room = "спортзал";
                                            }
                                            else if(lessonRoom.length > 1) {
//                                                lesson = lessonRoom[0];
//                                                room = lessonRoom[1];
                                                lesson = "";
                                                room = "";
                                                for(int i=0; i<lessonRoom.length-1; i++){
                                                    lesson += lessonRoom[i] + " ";
                                                }
                                                room = lessonRoom[lessonRoom.length-1];
                                            }

                                            if (content.contains("Расписание")) {
                                                isSchedulePassed = true;
                                            } else if (isSchedulePassed == true) {
                                                // строка с перечнем классов в параллели
                                                colNum++;
                                                Schedule schedule = new Schedule();
                                                String[] gradeLetter = content.split("");
                                                String grade, letter;
                                                if(gradeLetter.length == 2) {
                                                    grade = gradeLetter[0];
                                                    letter = gradeLetter[1];
                                                }
                                                else{
                                                    grade = gradeLetter[0] + gradeLetter[1];
                                                    letter = gradeLetter[2];
                                                }
                                                schedule.setGrade(grade);
                                                schedule.setLetter(letter);
                                                schedules.add(schedule);
                                            } else {
                                                if (content.equals("Пятница") && isFriday == false) {
                                                    isFriday = false;
                                                    for (int i = c1; i <c2; i++) {
                                                        Weekdays fridayLessons = new Weekdays();
                                                        fridayLessons.setWeekday("Пятница");
                                                        schedules.get(i).getWeekdays().add(fridayLessons);
                                                    }
                                                    isFriday = true;
                                                } else if (isFriday == true) {
                                                    LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                    schedules.get(curNum).getWeekdays().get(4).getLessons().add(lessonInfo);
                                                    curNum++;
                                                } else if (content.equals("Четверг") && isThursday == false) {
                                                    isThursday = false;
                                                    for (int i = c1; i < c2; i++) {
                                                        Weekdays thursdayLessons = new Weekdays();
                                                        thursdayLessons.setWeekday("Четверг");
                                                        schedules.get(i).getWeekdays().add(thursdayLessons);
                                                    }
                                                    isThursday = true;
                                                } else if (isThursday == true) {
                                                    LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                    schedules.get(curNum).getWeekdays().get(3).getLessons().add(lessonInfo);
                                                    curNum++;
                                                } else if (content.equals("Среда") && isWednesday == false) {
                                                    isWednesday = false;
                                                    for (int i = c1; i < c2; i++) {
                                                        Weekdays wednsdayLessons = new Weekdays();
                                                        wednsdayLessons.setWeekday("Среда");
                                                        schedules.get(i).getWeekdays().add(wednsdayLessons);
                                                    }
                                                    isWednesday = true;
                                                } else if (isWednesday == true) {
                                                    LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                    schedules.get(curNum).getWeekdays().get(2).getLessons().add(lessonInfo);
                                                    curNum++;
                                                } else if (content.equals("Вторник") && isTuesday == false) {
                                                    isTuesday = false;
                                                    for (int i = c1; i < c2; i++) {
                                                        Weekdays tuesdayLessons = new Weekdays();
                                                        tuesdayLessons.setWeekday("Вторник");
                                                        schedules.get(i).getWeekdays().add(tuesdayLessons);
                                                    }
                                                    isTuesday = true;
                                                } else if (isTuesday == true) {
                                                    LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                    schedules.get(curNum).getWeekdays().get(1).getLessons().add(lessonInfo);
                                                    curNum++;
                                                } else if (content.equals("Понедельник") && isMonday == false) {
                                                    c2 = c1 + colNum;
                                                    for (int i = c1; i < c2; i++) {
                                                        Weekdays mondayLessons = new Weekdays();
                                                        mondayLessons.setWeekday("Понедельник");
                                                        schedules.get(i).getWeekdays().add(mondayLessons);
                                                    }
                                                    isMonday = true;

                                                } else if (isMonday == true) {
                                                    LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                    schedules.get(curNum).getWeekdays().get(0).getLessons().add(lessonInfo);
                                                    curNum++;
                                                }


                                            }

                                            break;
                                        case Cell.CELL_TYPE_NUMERIC:
                                            content = String.valueOf(cell.getNumericCellValue());
                                            isLessonNum = true;
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            if (isLessonNum == true) {
                                                //emptyLessonNum++;
                                                //curNum = emptyLessonNum;
                                                curNum++;
                                            }
                                    }
                                }
                            }

                            c1 += colNum;
                        }

                        String foo = "Done?!";
                        List<String> corpuses = new ArrayList<>();
                        corpuses.add("ш1");
                        corpuses.add("ш2");
                        corpuses.add("ш3");
                        corpuses.add("ш4");
//                        corpusAdapter = new SpinnerAdapter(corpuses, getApplicationContext());
//                        corpusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, corpuses);
//                        corpusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCorpus.attachDataSource(corpuses);

                        List<String> weekdays = new ArrayList<>();
                        weekdays.add("Понедельник");
                        weekdays.add("Вторник");
                        weekdays.add("Среда");
                        weekdays.add("Четверг");
                        weekdays.add("Пятница");
//                        weekDayAdapter = new SpinnerAdapter(weekdays, getApplicationContext());
//                        weekDayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, weekdays);
//                        weekDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spWeekday.attachDataSource(weekdays);

                        List<String> grades = new ArrayList<>();
                        /*
                        grades.add("5");
                        grades.add("6");
                        grades.add("7");
                        grades.add("8");
                        grades.add("9");
                        grades.add("10");
                        grades.add("11");*/

                        List<String> letters = new ArrayList<>();
                        for (Schedule schedule: schedules) {
                            letters.add(schedule.getLetter());
                            grades.add(schedule.getGrade());
                        }
                        // удаляем дубликаты
                        HashSet<String> hashSet = new HashSet<String>();
                        hashSet.addAll(letters);
                        letters.clear();
                        letters.addAll(hashSet);

                        hashSet.clear();
                        hashSet.addAll(grades);
                        grades.clear();
                        grades.addAll(hashSet);

//                        gradeAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, grades);
//                        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        gradeAdapter = new SpinnerAdapter(grades, getApplicationContext());
                        spGrade.attachDataSource(grades);

//                        letterAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, letters);
//                        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        letterAdapter = new SpinnerAdapter(letters, getApplicationContext());
                        //spLetter.attachDataSource(letters);

                        //подгрузка букв первого класса
                        List<String> shownLetters = new ArrayList<>();
                        for (Schedule schedule: schedules) {
                            if(schedule.getGrade().equals(grades.get(0))){
                                shownLetters.add(schedule.getLetter());
                            }
                        }
                        spLetter.attachDataSource(shownLetters);
                        //Обработка подгрузки только существующих данных

                        spGrade.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                            @Override
                            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                                String selectedGrade = (String)parent.getItemAtPosition(position);
                                List<String> searchedLetters = new ArrayList<>();
                                for (Schedule item: schedules) {
                                    if(item.getGrade().equals(selectedGrade)){
                                        searchedLetters.add(item.getLetter());
                                    }
                                }
                                spLetter.attachDataSource(searchedLetters);
                            }
                        });


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void btnShowSchedule_Click(View view) {
        String corpus = spCorpus.getSelectedItem().toString();
        String grade = spGrade.getSelectedItem().toString();
        String letter = spLetter.getSelectedItem().toString();
        String weekday = spWeekday.getSelectedItem().toString();
        Boolean isFound=false;
        out.clear();
        for (Schedule item: schedules) {
            for (Weekdays itemWeekday: item.getWeekdays()) {
                if(item.getGrade().equals(grade) && item.getLetter().equals(letter) && itemWeekday.getWeekday().equals(weekday)){
                    out.addAll(itemWeekday.getLessons());
                    isFound = true;
                    break;
                }
            }
            if(isFound == true) break;
        }
        adapter.notifyDataSetChanged();

    }
}