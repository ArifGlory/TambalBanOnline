package com.fahrul.tambanonline.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahrul.tambanonline.MainActivity
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.activity.ProfilActivity
import com.fahrul.tambanonline.adapter.AdapterTambal
import com.fahrul.tambanonline.model.TambalBan
import com.google.firebase.firestore.Query
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_admin.*
import kotlinx.android.synthetic.main.activity_home_admin.rvTambal
import kotlinx.android.synthetic.main.activity_list_tambal.*

class HomeAdminActivity : BaseActivity() {

    var TAG_GET_TAMBAL = "getTambalBan"
    lateinit var adapter: AdapterTambal

    var listTambalBan = ArrayList<TambalBan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)
        adapter = AdapterTambal(listTambalBan)
        rvTambal.setHasFixedSize(true)
        rvTambal.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvTambal.adapter = adapter

        getDataTambalTerbaru()

        lineLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineAddTambal.setOnClickListener {
            startActivity(Intent(this, AddTambalActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineSemuaTambal.setOnClickListener {
            startActivity(Intent(this, ListTambalActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineProfilAdmin.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    fun getDataTambalTerbaru(){
        tambalRef
            .orderBy("created_at",Query.Direction.DESCENDING)
            .limit(3)
            .get().addOnSuccessListener { result ->
            listTambalBan.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var tambal : TambalBan = document.toObject(TambalBan::class.java)
                tambal.id_tambal = document.id
                listTambalBan.add(tambal)

            }
            if (listTambalBan.size == 0){
                animation_view_tambal_admin.setAnimation(R.raw.empty_box)
                animation_view_tambal_admin.playAnimation()
                animation_view_tambal_admin.loop(false)
            }else{
                animation_view_tambal_admin.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_TAMBAL,"err : "+exception.message)
        }
    }
}
