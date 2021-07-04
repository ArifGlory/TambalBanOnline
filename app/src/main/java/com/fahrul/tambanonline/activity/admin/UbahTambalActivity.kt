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
import com.bumptech.glide.Glide
import com.fahrul.tambanonline.R
import com.fahrul.tambanonline.model.SharedVariable
import com.fahrul.tambanonline.model.TambalBan
import com.fahrul.tambanonline.util.PermissionHelper
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_tambal.*
import kotlinx.android.synthetic.main.activity_detail_tambal.*
import kotlinx.android.synthetic.main.activity_ubah_tambal.*
import kotlinx.android.synthetic.main.activity_ubah_tambal.edAlamat
import kotlinx.android.synthetic.main.activity_ubah_tambal.edName
import kotlinx.android.synthetic.main.activity_ubah_tambal.ivProfile
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class UbahTambalActivity : BaseActivity(),PermissionHelper.PermissionListener {
    lateinit var i : Intent
    lateinit var tambal : TambalBan

    var TAG_UBAH = "ubahTambal"
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
        setContentView(R.layout.activity_ubah_tambal)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        i = intent

        tambal = i.getSerializableExtra("tambal") as TambalBan
        ivProfile.setOnClickListener {
            launchGallery()
        }
        btnUbahLokasi.setOnClickListener {
            val i = Intent(this,UbahLokasiActivity::class.java)
            i.putExtra("tambal",tambal as Serializable)
            startActivity(i)
        }
        btnUpdateData.setOnClickListener {
            checkValidation()
        }

        updateUI()
    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getAlamat = edAlamat.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        } else if (fileUri == null){
            tambal.nama_tambal = getName
            tambal.alamat = getAlamat
            updateWithoutFoto()
        }
        else {
            updateWithFoto()
        }
    }

    fun updateWithoutFoto(){
        showLoading(this)
        if (lat == 0.0){
            lat = tambal.lat.toDouble()
            lon = tambal.lon.toDouble()
        }
        tambalRef.document(tambal.id_tambal).update("nama_tambal",tambal.nama_tambal)
        tambalRef.document(tambal.id_tambal).update("alamat",tambal.alamat)
        tambalRef.document(tambal.id_tambal).update("lat",lat.toString())
        tambalRef.document(tambal.id_tambal).update("lon",lon.toString()).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah data berhasil")
                startActivity(Intent(this, ListTambalActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                finish()
            }else{
                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d(TAG_UBAH,"err : "+task.exception)
            }
        }
    }

    fun  updateWithFoto(){
        showLoading(this)
        if (fileUri != null){
            Log.d(TAG_UBAH,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_UBAH, exception.toString())
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
                        tambalRef.document(tambal.id_tambal).update("nama_tambal",tambal.nama_tambal)
                        tambalRef.document(tambal.id_tambal).update("alamat",tambal.alamat)
                        tambalRef.document(tambal.id_tambal).update("lat",lat.toString())
                        tambalRef.document(tambal.id_tambal).update("lon",lon.toString())
                        Log.d(TAG_UBAH,"download URL : "+ downloadUri.toString())// This is the one you should store
                        tambalRef.document(tambal.id_tambal).update("foto",url).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah data berhasil")
                                startActivity(Intent(this, ListTambalActivity::class.java))
                                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                                finish()
                            }else{
                                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                                Log.d(TAG_UBAH,"err : "+task.exception)
                            }
                        }
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

    fun updateUI(){

        Glide.with(this)
            .load(tambal.foto)
            .into(ivProfile)
        edName.setText(tambal.nama_tambal)
        edAlamat.setText(tambal.alamat)
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

            val img: Drawable = btnUbahLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnUbahLokasi.setText("Lokasi Telah diubah")
            btnUbahLokasi.setCompoundDrawables(img,null,null,null)
        }
    }

}
