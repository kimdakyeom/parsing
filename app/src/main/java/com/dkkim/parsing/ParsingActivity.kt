package com.dkkim.parsing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import android.widget.TextView
import android.os.StrictMode
import android.app.Activity
import android.view.View
import java.lang.Exception
import java.net.URL


class ParsingActivity() : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parsing)
        StrictMode.enableDefaults()
        val status1 = findViewById<View>(R.id.result) as TextView //파싱된 결과확인!
        var initem: Boolean = false
        var food_Code: Boolean = false
        var large_Name: Boolean = false
        var middle_Name: Boolean = false
        var food_Name: Boolean = false
        var code: String? = ""
        var large: String? = ""
        var middle: String? = ""
        var name: String?= ""
        try {
            val url = URL(
                "http://apis.data.go.kr/1390802/AgriFood/MzenFoodCode/getKoreanFoodList?serviceKey=GYbh7D2DFLK834K3R0f009ILwoUOVS2FjkM7JkOVpvbt7iNpeYKdlenp8wf3rEldx3Jt75r8z9zLByTqdJdzCA%3D%3D&Page_Size=10000"
            ) //검색 URL부분
            val parserCreator = XmlPullParserFactory.newInstance()
            val parser = parserCreator.newPullParser()
            parser.setInput(url.openStream(), null)
            var parserEvent = parser.eventType
            println("파싱시작합니다.")
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                when (parserEvent) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "food_Code") { //title 만나면 내용을 받을수 있게 하자
                            food_Code = true
                        }
                        if ((parser.name == "large_Name")) { //address 만나면 내용을 받을수 있게 하자
                            large_Name = true
                        }
                        if ((parser.name == "middle_Name")) { //mapx 만나면 내용을 받을수 있게 하자
                            middle_Name = true
                        }
                        if ((parser.name == "food_Name")) { //mapy 만나면 내용을 받을수 있게 하자
                            food_Name = true
                        }
                        if ((parser.name == "message")) { //message 태그를 만나면 에러 출력
                            status1.text = status1.text.toString() + "에러"
                            //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (food_Code) { //isTitle이 true일 때 태그의 내용을 저장.
                            code = parser.text
                            food_Code = false
                        }
                        if (large_Name) { //isAddress이 true일 때 태그의 내용을 저장.
                            large = parser.text
                            large_Name = false
                        }
                        if (middle_Name) { //isMapx이 true일 때 태그의 내용을 저장.
                            middle = parser.text
                            middle_Name = false
                        }
                        if (food_Name) { //isMapy이 true일 때 태그의 내용을 저장.
                            name = parser.text
                            food_Name = false
                        }
                    }
                    XmlPullParser.END_TAG -> if ((parser.name == "item")) {
                        status1.text =
                            (status1.text.toString() + "code : " + code + "\n large: " + large + "\n middle : " + middle
                                    + "\n name : " + name + "\n")
                        initem = false
                    }
                }
                parserEvent = parser.next()
            }
        } catch (e: Exception) {
            status1.text = "에러가..났습니다..."
        }
    }
}