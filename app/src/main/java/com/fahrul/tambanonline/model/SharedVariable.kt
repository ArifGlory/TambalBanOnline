package com.fahrul.tambanonline.model

import com.google.android.gms.maps.model.LatLng
import com.tapisdev.mysteam.model.UserModel

class SharedVariable {
    //open var selectedIdPenyedia = "-"

    companion object {
        lateinit var user : UserModel
        var centerLatLon : LatLng = LatLng(0.0,0.0)

    }
}