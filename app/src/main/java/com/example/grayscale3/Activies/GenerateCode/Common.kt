package com.example.grayscale3.Activies.GenerateCode

import android.content.Context
import com.example.grayscale.R
import java.io.File

object Common {

    fun getAppPath(context: Context): String{
        val dir = File(android.os.Environment.getExternalStorageDirectory().toString()
        +File.separator
        +context.resources.getString(R.string.app_name)
        +File.separator)
        if(!dir.exists()){
            dir.mkdir()
        }
        return dir.path+File.separator
    }

}