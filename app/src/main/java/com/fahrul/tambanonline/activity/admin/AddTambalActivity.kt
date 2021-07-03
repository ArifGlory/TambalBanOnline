package com.fahrul.tambanonline.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.model.SharedVariable
import com.fahrul.tambanonline.model.TambalBan
import com.fahrul.tambanonline.util.PermissionHelper
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_tambal.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddTambalActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_SIMPAN = "simpanTambal"
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentDate = sdf.format(Date())

    lateinit var tambal : TambalBan
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var lat = 0.0
    var lon = 0.0

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tambal)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        ivProfile.setOnClickListener {
            launchGallery()
        }
        btnLokasi.setOnClickListener {
            startActivity(Intent(this, SelectLokasiActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        btnDaftarkan.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getAlamat = edAlamat.text.toString()

        var dateTimeNow = ""+sdfTime.format(Date())

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        } else if (lat == 0.0){
            showErrorMessage("Lokasi belum dpilih")
        }else if (fileUri == null){
            showErrorMessage("anda belum memilih foto")
        }
        else {
            tambal = TambalBan(
                getName,
                getAlamat,
                "",
                lat.toString(),
                lon.toString(),
                "",
                dateTimeNow
            )
            uploadFoto()
        }
    }

    fun uploadFoto(){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_SIMPAN,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_SIMPAN, exception.toString())
            }.addOnSuccessListener {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                showSuccessMessage("Image Berhasil di upload")
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                    }

                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAu.getInstance().getCurrentUser().getUid());
                        val url = downloadUri!!.toString()
                        Log.d(TAG_SIMPAN,"download URL : "+ downloadUri.toString())// This is the one you should store
                        tambal.foto = url
                        saveTambalBan()
                    } else {
                        dismissLoading()
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pDialogLoading.setTitleText("Uploaded " + progress.toInt() + "%...")
            }


        }else{
            dismissLoading()
            showErrorMessage("Anda belum memilih file")
        }
    }

    fun saveTambalBan(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        tambalRef.document().set(tambal).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                var resetLokasi = LatLng(0.0,0.0)
                SharedVariable.centerLatLon = resetLokasi

                dismissLoading()
                showLongSuccessMessage("Tambah Tambal Ban Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            fileUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                fotoBitmap = bitmap
                ivProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        if (SharedVariable.centerLatLon.latitude != 0.0){
            lat = SharedVariable.centerLatLon.latitude
            lon = SharedVariable.centerLatLon.longitude

            val img: Drawable = btnLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnLokasi.setText("Lokasi Telah dipilih")
            btnLokasi.setCompoundDrawables(img,null,null,null)
        }
    }
}
