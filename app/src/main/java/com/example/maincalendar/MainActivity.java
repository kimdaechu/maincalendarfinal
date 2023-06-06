package com.example.maincalendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private Set<CalendarDay> selectedDates;

    private DatabaseReference databaseReference;

    private Button btn_select;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    // 데이터베이스 저장 경로
    DatabaseReference ref = database.getReference("server/saving-data");

    private String nickname; // 수정된 부분: nickname 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("nickname")) {
            nickname = intent.getStringExtra("nickname"); // 수정된 부분: nickname 값 가져오기
        }

        btn_select = findViewById(R.id.btn_select);

        databaseReference = FirebaseDatabase.getInstance().getReference("server/saving-data");

        calendarView = findViewById(R.id.calendarView);
        selectedDates = new HashSet<>();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                checkDateAvailability(date.getDate());
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedDataToFirebase();
            }
        });

        // 토요일과 일요일 표시
        calendarView.addDecorator(new SaturdayDecorator());
        calendarView.addDecorator(new SundayDecorator());
        calendarView.addDecorator(new EventDecorator());
    }

    private void saveSelectedDataToFirebase() {
        // 선택한 날짜 리스트를 생성
        List<String> selectedDatesList = new ArrayList<>();
        for (CalendarDay selectedDay : selectedDates) {
            Date date = selectedDay.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(date);
            selectedDatesList.add(formattedDate);
        }

        // 파이어베이스 데이터베이스에 날짜 저장
        DatabaseReference dateRef = databaseReference.child("dates");
        dateRef.setValue(selectedDatesList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "날짜가 저장되었습니다.", Toast.LENGTH_SHORT).show();

                        // 다음 액티비티로 이동
                        Intent intent = new Intent(MainActivity.this, NextActivity.class);
                        intent.putStringArrayListExtra("selectedDates", new ArrayList<>(selectedDatesList));
                        intent.putExtra("nickname", nickname); // 수정된 부분: 카카오 닉네임 추가
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "날짜 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkDateAvailability(Date date) {
        CalendarDay day = CalendarDay.from(date);
        if (selectedDates.contains(day)) {
            selectedDates.remove(day);
        } else {
            selectedDates.add(day);
        }
        calendarView.invalidateDecorators(); // 데코레이터 갱신
    }

    private class SaturdayDecorator implements DayViewDecorator {
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar calendar = day.getCalendar();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE)); // 날짜 숫자 색상 변경
        }
    }

    private class SundayDecorator implements DayViewDecorator {
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar calendar = day.getCalendar();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED)); // 날짜 숫자 색상 변경
        }
    }

    private class EventDecorator implements DayViewDecorator {
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return selectedDates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(getResources().getDrawable(R.drawable.green_circle));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.invalidateDecorators(); // 데코레이터 갱신
    }
}
