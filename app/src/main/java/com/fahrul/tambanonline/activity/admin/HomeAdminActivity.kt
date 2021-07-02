package com.fahrul.tambanonline.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fahrul.tambanonline.MainActivity
import com.fahrul.tambanonline.R
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_admin.*

class HomeAdminActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)


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
    }
}
