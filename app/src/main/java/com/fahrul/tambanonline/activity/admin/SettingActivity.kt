package com.fahrul.tambanonline.activity.admin

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.fahrul.tambanonline.R
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    lateinit var dialog : Dialog
    var teksInformasi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mUserPref = UserPreference(this)

        cardTeksInfomasi.setOnClickListener {
            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_setting_info)

            val btnSimpan = dialog.findViewById(R.id.btnSimpan) as Button
            val etTeksInformasi = dialog.findViewById(R.id.etTeksInformasi) as EditText
            etTeksInformasi.setText(teksInformasi)

            btnSimpan.setOnClickListener {
                var getTeksInformasi = etTeksInformasi.text.toString()
                if (getTeksInformasi.equals("") || getTeksInformasi.length == 0){
                    showErrorMessage("Teks Informasi Belum diisi")
                }else{
                    saveDataInfo(getTeksInformasi)
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

       getDataInfoListTambal()
    }

    fun saveDataInfo(teks_informasi :String){
        showLoading(this)
        settingsRef.document("pengumuman").update("list_tambal",teks_informasi).addOnCompleteListener {
            task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Data berhasil Disimpan")
                getDataInfoListTambal()
            }else{
                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                Log.d("settingRef",""+task.exception)
            }
        }
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
}
