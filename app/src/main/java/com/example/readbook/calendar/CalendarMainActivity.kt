package com.example.readbook.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarMainActivity : AppCompatActivity() {
    private lateinit var texMonth: TextView
    private lateinit var calendarAdapter:CalendarAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_main)

        val calendar = Calendar.getInstance()
        calendarAdapter = CalendarAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        texMonth = findViewById(R.id.text_month)
        recyclerView.layoutManager= GridLayoutManager(this, 7)
        //GridLayoutManager 달력이기 때문에 바둑판형식으로 해줌
        recyclerView.adapter = calendarAdapter

        val btnLeft : ImageButton = findViewById<ImageButton>(R.id.btn_left)
        val btnRight : ImageButton = findViewById<ImageButton>(R.id.btn_right)
        btnLeft.setOnClickListener {
            var month =  calendar.get(Calendar.MONTH) - 1
            var year = calendar.get(Calendar.YEAR)
            if(month == -1){
                month == 11
                year = year - 1
            }
            calendar.set(year, month, calendar.get(Calendar.DAY_OF_MONTH))
            calendarShow(calendar)
        }
        btnRight.setOnClickListener {
            var month = calendar.get(Calendar.MONTH) + 1
            var year = calendar.get(Calendar.YEAR)
            if(month == 12){
                month = 0
                year = year + 1
            }
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            calendarShow(calendar)
        }

        calendarShow(calendar)
    }

    private fun calendarShow(calendar: Calendar){
        val newCalendar = Calendar.getInstance()
        newCalendar.timeInMillis = calendar.timeInMillis
        texMonth.setText(SimpleDateFormat("yyyy.MM").format(newCalendar.time))
        val firstDay = newCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDay = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val arrayDay = ArrayList<Long>()

        for (i: Int in firstDay..lastDay){
            newCalendar.set(Calendar.DAY_OF_MONTH,i)
            val dayOfWeek = newCalendar.get(Calendar.DAY_OF_WEEK)
            if (i == 1 && dayOfWeek > 1) {
                for(j: Int in 1..dayOfWeek -1) {
                    val lastCalendar = Calendar.getInstance()
                    var month = newCalendar.get(Calendar.MONTH) - 1
                    var year = newCalendar.get(Calendar.YEAR)
                    if(month == -1){
                        month = 11
                        year =year - 1
                    }
                    lastCalendar.set(Calendar.YEAR, year)
                    lastCalendar.set(Calendar.MONTH, month)
                    val lastMonth_lastDay =
                        (lastCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - (j - 1))
                    lastCalendar.set(Calendar.DAY_OF_MONTH, lastMonth_lastDay)
                    arrayDay.add(lastCalendar.timeInMillis)
                    Collections.sort(arrayDay)
                }
            }
            arrayDay.add(newCalendar.timeInMillis)

        }
        calendarAdapter.setList(arrayDay, newCalendar.get(Calendar.MONTH))

    }
}