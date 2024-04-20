package com.example.gocart

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.gocart.databinding.DialogBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var dialog : AlertDialog? = null
    fun showToast(context:Context ,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    fun showDialog(context:Context ,message:String){
        val progress = DialogBinding.inflate(LayoutInflater.from(context))
        progress.dialogText.text =message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }


    fun hideDialog(){
        dialog?.dismiss()
    }


    private var firebaseAuthInstance : FirebaseAuth? =null
    fun getAuthInstance():FirebaseAuth{
        if(firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

}