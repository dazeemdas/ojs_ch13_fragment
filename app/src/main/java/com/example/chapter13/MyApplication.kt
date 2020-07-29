package com.example.chapter13

import android.app.Application
import io.realm.Realm

class MyApplication: Application() {
    /**
     * 앱을 실행하면 가장 먼저 실행되는 애플리케이션 객체를 상속하여 Realm을 초기화해야 합니다.
     * 매니페스트 파일의 application 태그 안에 android:name 속성을 추가합니다.
     */
    override fun onCreate() {   // 액티비티 생성 전 호출됩니다.
        super.onCreate()
        Realm.init(this)
    }
}