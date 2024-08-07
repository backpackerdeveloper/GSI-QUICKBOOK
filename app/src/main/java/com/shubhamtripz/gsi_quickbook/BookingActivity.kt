package com.shubhamtripz.gsi_quickbook

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhamtripz.gsi_quickbook.databinding.ActivityBookingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var selectedDate: String
    private var selectedTimeSlots: MutableList<String> = mutableListOf()
    private lateinit var selectedTurfName: String
    private val firestore = FirebaseFirestore.getInstance()
    private val timeSlots = listOf(
        "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
        "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00",
        "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00",
        "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00",
        "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking)

        val turf: ParcelableTurf? = intent.getParcelableExtra("TURF")
        if (turf != null) {
            binding.turf = turf
            selectedTurfName = turf.name  // Store turf name

            binding.imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.imagesRecyclerView.adapter = ImageAdapter(turf.images)

            // Set Dates
            setDates()

            // Handle Confirm Booking
            binding.confirmButton.setOnClickListener {
                confirmBooking(turf)
            }
        } else {
            Toast.makeText(this, "Error: No turf data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDates() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val today = calendar.time
        val tomorrow = calendar.apply { add(Calendar.DAY_OF_YEAR, 1) }.time
        val dayAfterTomorrow = calendar.apply { add(Calendar.DAY_OF_YEAR, 1) }.time

        binding.todayDateTextView.text = dateFormat.format(today)
        binding.tomorrowDateTextView.text = dateFormat.format(tomorrow)
        binding.dayAfterTomorrowDateTextView.text = dateFormat.format(dayAfterTomorrow)

        // Set click listeners for date selection
        binding.todayDateTextView.setOnClickListener { selectDate(dateFormat.format(today), binding.todayDateTextView) }
        binding.tomorrowDateTextView.setOnClickListener { selectDate(dateFormat.format(tomorrow), binding.tomorrowDateTextView) }
        binding.dayAfterTomorrowDateTextView.setOnClickListener { selectDate(dateFormat.format(dayAfterTomorrow), binding.dayAfterTomorrowDateTextView) }
    }

    private fun selectDate(date: String, selectedTextView: TextView) {
        selectedDate = date
        loadAvailableTimeSlots()

        // Reset background tint of all date TextViews
        resetDateTextViewsBackground()

        // Set background tint of the selected TextView
        selectedTextView.setBackgroundColor(getColor(R.color.light_green))  // Replace with your light green color resource
    }

    private fun resetDateTextViewsBackground() {
        binding.todayDateTextView.setBackgroundColor(getColor(android.R.color.white))
        binding.tomorrowDateTextView.setBackgroundColor(getColor(android.R.color.white))
        binding.dayAfterTomorrowDateTextView.setBackgroundColor(getColor(android.R.color.white))
    }

    private fun loadAvailableTimeSlots() {
        binding.slotsContainer.removeAllViews()
        selectedTimeSlots.clear()  // Clear previously selected slots

        val bookingRef = firestore.collection("bookings").document(selectedDate)
        bookingRef.get().addOnSuccessListener { document ->
            val bookedSlots = document?.get("slots") as? List<String> ?: emptyList()

            for (slot in timeSlots) {
                if (slot !in bookedSlots) {
                    val checkBox = CheckBox(this).apply {
                        text = slot
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedTimeSlots.add(slot)
                            } else {
                                selectedTimeSlots.remove(slot)
                            }
                        }
                    }
                    binding.slotsContainer.addView(checkBox)
                }
            }
        }
    }

    private fun confirmBooking(turf: ParcelableTurf) {
        if (!::selectedDate.isInitialized || selectedTimeSlots.isEmpty()) {
            Toast.makeText(this, "Please select a date and time slots", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingRef = firestore.collection("bookings").document(selectedDate)
        bookingRef.get().addOnSuccessListener { document ->
            val bookedSlots = document?.get("slots") as? List<String> ?: emptyList()
            val newSlots = bookedSlots + selectedTimeSlots

            bookingRef.set(mapOf(
                "turfName" to selectedTurfName,
                "slots" to newSlots
            ))
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
