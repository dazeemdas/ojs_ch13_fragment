package com.example.chapter13

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Todo (               //코틀린에서는 Realm에서 사용하는 클래스에 open 키워드를 추가합니다.
    @PrimaryKey var id: Long = 0, // id는 유일한 값이 되어야 하기 때문에 기본키 제약을 주석으로 추가합니다.
    var title: String = "",
    var date: Long = 0
): RealmObject()                  // RealmObject 클래스를 상속받아 Realm 데이터베이스에서 다룰 수 있습니다.