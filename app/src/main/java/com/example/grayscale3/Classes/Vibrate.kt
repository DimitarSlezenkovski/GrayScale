package com.example.grayscale3.Classes

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class Vibrate {

   fun vibratePhone(context: Context){
       val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
       if (Build.VERSION.SDK_INT >= 26) {
           vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
       } else {
           vibrator.vibrate(200)
       }
   }

}