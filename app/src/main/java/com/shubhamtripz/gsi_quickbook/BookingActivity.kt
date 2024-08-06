package com.shubhamtripz.gsi_quickbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhamtripz.gsi_quickbook.databinding.ActivityBookingBinding
import java.util.Calendar

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var selectedDate: String
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking)

        val turf: ParcelableTurf? = intent.getParcelableExtra("TURF")
        if (turf != null) {
            binding.turf = turf

            binding.imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.imagesRecyclerView.adapter = ImageAdapter(turf.images)

            binding.datePickerButton.setOnClickListener {
                showDatePicker()
            }

            binding.confirmButton.setOnClickListener {
                confirmBooking(turf)
            }
        } else {
            Toast.makeText(this, "Error: No turf data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            binding.selectedDateTextView.text = selectedDate
        }, year, month, day).show()
    }

    private fun confirmBooking(turf: ParcelableTurf) {
        if (!::selectedDate.isInitialized) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingRef = firestore.collection("bookings").document(selectedDate)
        bookingRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val isBooked = document.getString("status") == "booked"
                if (isBooked) {
                    Toast.makeText(this, "Date is already booked. Choose another date.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Date is available but not yet booked. Booking now...", Toast.LENGTH_SHORT).show()
                    bookingRef.set(mapOf("turfId" to turf.name, "status" to "booked"))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                bookingRef.set(mapOf("turfId" to turf.name, "status" to "booked"))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
