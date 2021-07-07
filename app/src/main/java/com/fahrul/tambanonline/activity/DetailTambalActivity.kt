package com.fahrul.tambanonline.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.activity.admin.UbahTambalActivity
import com.fahrul.tambanonline.model.TambalBan
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_tambal.*
import java.io.Serializable


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
            /*var urlMap = "geo:0,0?q="+tambal.lat+","+tambal.lon+"("+tambal.nama_tambal+")"
            Log.d("infomap"," "+urlMap)
            val gmmIntentUri = Uri.parse(urlMap)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }*/
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(tambal.url_map)
            )
            startActivity(intent)
        }
        cvHapus.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin menghapus ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)
                    tambalRef.document(tambal.id_tambal).delete().addOnSuccessListener {
                        dismissLoading()
                        showSuccessMessage("Data berhasil dihapus")
                        onBackPressed()
                        Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                    }.addOnFailureListener {
                            e ->
                        dismissLoading()
                        showErrorMessage("terjadi kesalahan "+e)
                        Log.w("deleteDoc", "Error deleting document", e)
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }
        cvUbahData.setOnClickListener {
            val i = Intent(this,UbahTambalActivity::class.java)
            i.putExtra("tambal",tambal as Serializable)
            startActivity(i)
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
