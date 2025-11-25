package com.example.readium.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context

object FirebaseConfig {
    private var _auth: FirebaseAuth? = null
    private var _firestore: FirebaseFirestore? = null
    
    val auth: FirebaseAuth
        get() = _auth ?: throw IllegalStateException("Firebase não foi inicializado")
    
    val firestore: FirebaseFirestore
        get() = _firestore ?: throw IllegalStateException("Firebase não foi inicializado")
    
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val app = FirebaseApp.initializeApp(context)
                println("Firebase inicializado: ${app?.name}")
            } else {
                println("Firebase já estava inicializado")
            }
            
            _auth = FirebaseAuth.getInstance()
            _firestore = FirebaseFirestore.getInstance()
            
            println("Firebase Auth e Firestore configurados com sucesso")
        } catch (e: Exception) {
            println("Erro ao inicializar Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
    
    fun isInitialized(): Boolean {
        return _auth != null && _firestore != null
    }
}