package com.example.maincalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NextActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var btnInvite: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        textView = findViewById(R.id.textView)
        btnInvite = findViewById(R.id.btnInvite)

        // 인텐트에서 선택한 날짜 리스트와 카카오 닉네임을 받아옴
        val selectedDates = intent.getStringArrayListExtra("selectedDates")
        val nickname = intent.getStringExtra("nickname")

        // 선택한 날짜들과 카카오 닉네임을 TextView에 표시
        val stringBuilder = StringBuilder()
        if (nickname != null) {
            stringBuilder.append("$nickname 님이 ")
        } else {
            stringBuilder.append("Unknown 님이 ")
        }
        stringBuilder.append("고르신 날짜입니다!\n")

        if (selectedDates != null && selectedDates.isNotEmpty()) {
            for (date in selectedDates) {
                stringBuilder.append(date).append("\n")
            }
        } else {
            stringBuilder.append("날짜가 선택되지 않았습니다.")
        }

        textView.text = stringBuilder.toString()

        // Invitelink 액티비티로 이동
        btnInvite.setOnClickListener {
            val intent = Intent(this, Invitelink::class.java)
            startActivity(intent)
        }
    }
}