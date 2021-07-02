package com.fahrul.tambanonline.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fahrul.tambanonline.MainActivity
import com.fahrul.tambanonline.R
import com.tapisdev.cateringtenda.base.BaseActivity

class MaintenanceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
    }
}
