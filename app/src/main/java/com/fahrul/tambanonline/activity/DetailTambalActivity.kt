package com.fahrul.tambanonline.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.model.TambalBan
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_tambal.*

class DetailTambalActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var tambal : TambalBan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tambal)
        mUserPref = UserPreference(this)
        i = intent

        tambal = i.getSerializableExtra("tambal") as TambalBan

        cvCekLokasi.setOnClickListener {

        }
        ivBack.setOnClickListener {
            onBackPressed()
        }

        updateUI()
    }


    fun updateUI(){
        if (mUserPref.getJenisUser().equals("admin")){
            cvHapus.visibility = View.VISIBLE
            cvUbahData.visibility = View.VISIBLE
        }
        tvNamaTambal.setText(tambal.nama_tambal)
        tvAlamat.setText(tambal.alamat)
        Glide.with(this)
            .load(tambal.foto)
            .into(ivTambal)

    }
}
