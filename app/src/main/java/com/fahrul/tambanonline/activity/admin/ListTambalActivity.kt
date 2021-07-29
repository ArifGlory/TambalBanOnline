package com.fahrul.tambanonline.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.adapter.AdapterTambal
import com.fahrul.tambanonline.model.TambalBan
import com.google.firebase.firestore.Query
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_tambal.*

class ListTambalActivity : BaseActivity() {


    var TAG_GET_TAMBAL = "getTambalBan"
    var teksInformasi = ""
    lateinit var adapter: AdapterTambal

    var listTambalBan = ArrayList<TambalBan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_tambal)

        mUserPref = UserPreference(this)
        adapter = AdapterTambal(listTambalBan)
        rvTambal.setHasFixedSize(true)
        rvTambal.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvTambal.adapter = adapter


        getDataTambal()
        getDataInfoListTambal()
    }

    fun getDataInfoListTambal(){
        pgSetting.visibility = View.VISIBLE
        cardTeksInfomasi.isEnabled = false

        settingsRef.document("pengumuman").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                pgSetting.visibility = View.GONE
                cardTeksInfomasi.isEnabled = true

                teksInformasi = task.result?.get("list_tambal").toString()
                if (teksInformasi != null) {
                    tvInfoListTambal.setText(""+teksInformasi)
                }
            }else{
                pgSetting.visibility = View.GONE
                cardTeksInfomasi.isEnabled = true
            }
        }
    }

    fun getDataTambal(){
        tambalRef.orderBy("created_at",Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
            listTambalBan.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var tambal : TambalBan = document.toObject(TambalBan::class.java)
                tambal.id_tambal = document.id
                listTambalBan.add(tambal)

            }
            if (listTambalBan.size == 0){
                animation_view_tambal.setAnimation(R.raw.empty_box)
                animation_view_tambal.playAnimation()
                animation_view_tambal.loop(false)
            }else{
                animation_view_tambal.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_TAMBAL,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataTambal()
    }
}
