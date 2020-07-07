package com.example.screenshotshare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var cardView: CardView
    private lateinit var screenshot:Button
    private lateinit var share:Button
    private lateinit var file:File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cardView=findViewById(R.id.cardview)
        screenshot=findViewById(R.id.screenshot_button)
        share=findViewById(R.id.share_button)

        screenshot.setOnClickListener {
            Toast.makeText(this,"taking screenshot",Toast.LENGTH_SHORT).show()
            takeScreenShot()
        }

        share.setOnClickListener {
            Toast.makeText(this,"sharing",Toast.LENGTH_SHORT).show()
            share(file)
        }

    }
    private fun takeScreenShot(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)

        val bitmap= Bitmap.createBitmap(cardView.width,cardView.height, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)
        cardView.draw(canvas)

        val  mainDirectoryname:File=File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ScreesnShots")
        if (!mainDirectoryname.exists()){
            if (mainDirectoryname.mkdirs()){
                Log.e("Create Directory","Main Directory created: "+mainDirectoryname)
            }
        }

        val name:String="screenshot"+ Calendar.getInstance().time.toString()+".jpg"
        val dir :File=File(mainDirectoryname.absolutePath)
        if (!dir.exists()){
            dir.mkdirs()
        }
        val imagefile:File= File(mainDirectoryname.absolutePath,name)
        val outPutStream:FileOutputStream = FileOutputStream(imagefile)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outPutStream)
        file=imagefile
        Toast.makeText(this,"FIle saved in directory",Toast.LENGTH_SHORT).show()
        outPutStream.flush()
        outPutStream.close()



    }
    private fun share(imagefile: File){

      //  val uri:Uri=Uri.fromFile(imagefile)
        val fileuri:Uri=FileProvider.getUriForFile(this,"com.example.screenshotshare.provider",imagefile)

        val intent=Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_STREAM,fileuri)
        startActivity(Intent.createChooser(intent,"Share Screenshot"))

    }
}