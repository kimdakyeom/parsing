package com.dkkim.parsing

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import android.widget.TextView
import android.widget.EditText
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    var editText: EditText? = null
    var textView: TextView? = null
    var data: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById<View>(R.id.editText) as EditText
        textView = findViewById<View>(R.id.textView) as TextView
    }

    // 버튼을 클릭했을 때 쓰레드를 생성하여 해당 함수를 실행하여 텍스트뷰에 데이터 출력
    fun buttonClicked(v: View) {
        when (v.getId()) {
            R.id.button ->
                // 쓰레드를 생성하여 돌리는 구간
                Thread {
                    data = getData() // 하단의 getData 메소드를 통해 데이터를 파싱
                    runOnUiThread { textView!!.text = data }
                }.start()
        }
    }

    @JvmName("getData1")
    fun getData(): String {
        val buffer = StringBuffer()
        val str = editText!!.text.toString() // EditText에 작성된 Text 얻어오기
        val location: String = URLEncoder.encode(str) //한글의 경우 인식이 안되서 utf-8 방식으로 encoding
        val queryUrl =
            ("http://apis.data.go.kr/1390802/AgriFood/MzenFoodCode/getKoreanFoodList?serviceKey=GYbh7D2DFLK834K3R0f009ILwoUOVS2FjkM7JkOVpvbt7iNpeYKdlenp8wf3rEldx3Jt75r8z9zLByTqdJdzCA%3D%3D&Page_Size=100000")

        try {
            val url = URL(queryUrl) // 문자열로 된 요청 url을 URL 객체로 생성.
            val input: InputStream = url.openStream() // url 위치로 인풋스트림 연결
            val factory = XmlPullParserFactory.newInstance()
            val xpp = factory.newPullParser()
            xpp.setInput(InputStreamReader(input, "UTF-8")) // inputstream 으로부터 xml 입력받기
            var tag: String
            xpp.next()
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_DOCUMENT -> buffer.append("파싱 시작 단계 \n\n")
                    XmlPullParser.START_TAG -> {
                        tag = xpp.name // 태그 이름 얻어오기
                        if (tag == "item")
                        else if (tag == "food_Code") {
                            buffer.append("code : ")
                            xpp.next() // food_Code 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(xpp.text)
                            buffer.append("\n") // 줄바꿈 문자 추가
                        } else if (tag == "large_Name") {
                            buffer.append("large : ")
                            xpp.next()
                            buffer.append(xpp.text)
                            buffer.append("\n")
                        } else if (tag == "middle_Name") {
                            buffer.append("middle :")
                            xpp.next()
                            buffer.append(xpp.text)
                            buffer.append("\n")
                        } else if (tag == "food_Name") {
                            buffer.append("name :")
                            xpp.next()
                            buffer.append(xpp.text)
                            buffer.append("\n")
                        }
                    }
                    XmlPullParser.TEXT -> {
                    }
                    XmlPullParser.END_TAG -> {
                        tag = xpp.name // 태그 이름 얻어오기
                        if (tag == "item") buffer.append("\n") // 첫번째 검색결과종료 후 줄바꿈
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        buffer.append("파싱 종료 단계 \n")
        return buffer.toString() // 파싱 다 종료 후 StringBuffer 문자열 객체 반환
    }
}