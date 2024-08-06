package com.shubhamtripz.gsi_quickbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class TurfListViewModel : ViewModel() {
    private val _turfList = MutableLiveData<List<Turf>>()
    val turfList: LiveData<List<Turf>> get() = _turfList

    init {
        loadTurfs()
    }

    private fun loadTurfs() {
        FirebaseFirestore.getInstance().collection("places")
            .get()
            .addOnSuccessListener { result ->
                val turfs = result.map { document ->
                    document.toObject(Turf::class.java)
                }
                _turfList.value = turfs
            }
    }
}
