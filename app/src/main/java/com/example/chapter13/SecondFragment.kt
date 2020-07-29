package com.example.chapter13

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_second.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private val realm = Realm.getDefaultInstance()  // Realm 클래스를 초기화했으면 인스턴스를 얻을 수 있습니다
    private val calendar: Calendar = Calendar.getInstance() // 날짜를 다룰 캘린더 객체

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 업데이트 조건
        val id = arguments?.getLong("id", -1L) ?: -1L
        if (id == -1L) {
            insertMode()
        } else {
            updateMode(id)
        }

        // 캘린더 뷰의 날짜를 선택했을 때 Calendar 객체에 설정
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    private fun insertMode() {
        /**
         * 추가 모드 초기화
         */
        // 삭제 버튼 감추기
        deleteFab.hide()

        // 완료 버튼을 클릭하면 추가
        doneFab.setOnClickListener { insertTodo() }
    }

    private fun updateMode(id: Long) {
        /**
         * 수정 모드 초기화
         */

        // id에 해당하는 객체를 화면에 표시
        Log.d("SecondFragment", "$id 수정 모드 초기화")
        val todo = realm.where<Todo>().equalTo("id", id).findFirst()!!
        todoEditText.setText(todo.title)
        calendarView.date = todo.date

        // 완료 버튼을 클릭하면 수정
        doneFab.setOnClickListener { updateTodo(id) }

        // 삭제 버튼을 클릭하면 삭제
        deleteFab.setOnClickListener { deleteTodo(id) }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()   // 액티비티 소멸 시 인스턴스를 해제합니다.
        Log.d("SecondFragment", "realm 인스턴스 해제")
    }

    private fun insertTodo() {
        realm.beginTransaction()    // 트랜잭션 시작

        val newItem = realm.createObject<Todo>(nextId())   // 새 객체 생성
        // 값 설정
        newItem.title = todoEditText.text.toString()
        newItem.date = calendar.timeInMillis

        realm.commitTransaction()   // 트랜잭션 종료 반영

        // 다이얼로그 표시
        alert("내용이 추가되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun updateTodo(id: Long) {
        realm.beginTransaction()    // 트랜잭션 시작

        val updateItem = realm.where<Todo>().equalTo("id", id).findFirst()!!
        // 값 수정
        updateItem.title = todoEditText.text.toString()
        updateItem.date = calendar.timeInMillis

        realm.commitTransaction()   // 트랜잭션 종료 반영

        // 다이얼로그 표시
        alert("내용이 변경되었습니다") {
            yesButton { finish() }
        }.show()
    }

    private fun deleteTodo(id: Long) {
        realm.beginTransaction()
        val deleteItem = realm.where<Todo>().equalTo("id", id).findFirst()!!

        //삭제할 객체
        deleteItem.deleteFromRealm()    // 삭제
        realm.commitTransaction()

        alert("내용이 삭제되었습니다") {
            yesButton { finish() }
        }.show()
    }

    // 다음 id를 반환
    private fun nextId(): Int {
        /**
         * Realm은 기본키 자동 증가를 지원하지 않습니다.
         * 객체 생성 시 id값을 계산하는 데 사용됩니다.
         * Todo 테이블의 모든 값을 얻으려면 where<Todo>() 메서드를 사용합니다.
         * 이 메서드는 RealmQuery 객체를 반환하고 다음에 이어지는 조건을 수행합니다.
         * 여기서는 max() 메서드를 조건으로 달았습니다. max()메서드는 현재 id 중 가장 큰 값을 얻을 때
         * 사용합니다.
         */
        val maxId = realm.where<Todo>().max("id")
        if (maxId != null) {
            return maxId.toInt() + 1
        }
        return 0
    }

    private fun finish(){
        findNavController().navigateUp()
    }
}