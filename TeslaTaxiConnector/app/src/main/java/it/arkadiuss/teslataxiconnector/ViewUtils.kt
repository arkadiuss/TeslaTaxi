package it.arkadiuss.teslataxiconnector

import android.app.AlertDialog
import android.content.Context
import android.view.MotionEvent
import android.view.View

fun View.setOnPressing(onStart: () -> Unit, onEnd: () -> Unit) {
    this.setOnTouchListener{ v: View,
                             m: MotionEvent ->
        when (m.actionMasked) {
            MotionEvent.ACTION_DOWN -> onStart()
            MotionEvent.ACTION_UP -> onEnd()
        }
        true
    }
}

fun Context.showDialog(builder: (AlertDialog.Builder) -> Unit) {
    AlertDialog.Builder(this).apply {
        builder(this)
    }.show()
}