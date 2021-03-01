package com.example.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatting.Adapter.ChatLeftYou
import com.example.chatting.Adapter.ChatRightMe
import com.example.chatting.Model.ChatModel
import com.example.chatting.Model.ChatNewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = ChatRoomActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        auth = FirebaseAuth.getInstance()

        val myUid = auth.uid
        val yourUid = intent.getStringExtra("yourUid")
        val name = intent.getStringExtra("name")


        val adapter = GroupAdapter<GroupieViewHolder>()

//        val db = FirebaseFirestore.getInstance()
//        //데이터 불러오기
//        db.collection("message")
//                .orderBy("time")
//                .get()
//                .addOnSuccessListener{result->
//                    for(document in result) {
//                        Log.d(TAG, document.toString())
//
//                        //메세지 전송한대로 읽어옴
//                        val senderUid = document.get("myUid")
//                        val msg = document.get("message").toString()
//
//                        Log.d(TAG,senderUid.toString())
//                        Log.d(TAG,myUid.toString())
//
//                        //내가 보낸 메세지일때
//                        if(senderUid!!.equals(myUid)){
//                            adapter.add(ChatRightMe(msg))
//                        }
//                        //내가 보낸메세지가 아닐때 -> 상대방이 보낸 메세지 일때
//                        else{
//                            adapter.add(ChatLeftYou(msg))
//                        }
//                    }
//                    recyclerView_chat.adapter = adapter
//
//                }

        val database = Firebase.database
        val myRef = database.getReference("message")
        val readRef = database.getReference("message").child(myUid.toString()).child(yourUid.toString())

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            //메세지를 전송하면 바로 화면에 출력될수 있도록 함
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG,"snapshot : "+snapshot)

                val model = snapshot.getValue(ChatNewModel::class.java)
                val msg = model?.message.toString()
                val who = model?.who

                if(who == "me"){
                    adapter.add(ChatRightMe(msg))
                }
                else{
                    adapter.add(ChatLeftYou(msg))
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        }

        recyclerView_chat.adapter = adapter
        readRef.addChildEventListener(childEventListener)

        val myRef_list = database.getReference("message-user-list")

        button2.setOnClickListener {
            //realtime database로 메세지 전송
            val message = editTextTextPersonName2.text.toString()

            //메세지 전송을 알려줌
            val chat = ChatNewModel(myUid.toString(), yourUid.toString(), message, System.currentTimeMillis(),"me")
            myRef.child(myUid.toString()).child(yourUid.toString()).push().setValue(chat)

            //메세지 받음을 알려줌
            val chat_get = ChatNewModel(yourUid.toString(), myUid.toString(), message, System.currentTimeMillis(),"you")
            myRef.child(yourUid.toString()).child(myUid.toString()).push().setValue(chat_get)

            //업데이트 되기만 함 
            myRef_list.child(myUid.toString()).child(yourUid.toString()).setValue(chat)

            editTextTextPersonName2.setText("")


//            val message = editTextTextPersonName2.text.toString()
//
//            editTextTextPersonName2.setText("")
//            val chat = ChatModel(myUid.toString(), yourUid.toString(), message, System.currentTimeMillis())
//
//            db.collection("message")
//                    .add(chat)
//                    .addOnSuccessListener {
//                        Log.d(TAG,"전송 성공")
//                    }
//                    .addOnFailureListener {
//                        Log.d(TAG,"전송 실패")
//                    }
        }
    }
}