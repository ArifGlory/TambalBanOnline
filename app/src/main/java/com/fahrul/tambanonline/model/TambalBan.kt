package com.fahrul.tambanonline.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TambalBan(
    var nama_tambal : String = "",
    var alamat : String = "",
    var foto : String = "",
    var lat : String = "",
    var lon : String = "",
    var id_tambal : String = "",
    var created_at : String = ""
) : Parcelable,java.io.Serializable