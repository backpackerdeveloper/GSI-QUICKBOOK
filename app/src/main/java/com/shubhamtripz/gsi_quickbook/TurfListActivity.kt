package com.shubhamtripz.gsi_quickbook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubhamtripz.gsi_quickbook.databinding.ActivityTurfListBinding

class TurfListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTurfListBinding
    private lateinit var viewModel: TurfListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_turf_list)
        viewModel = ViewModelProvider(this).get(TurfListViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = TurfAdapter(emptyList()) { turf ->
            Toast.makeText(this, "Booked ${turf.name}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra("TURF", ParcelableTurf.fromTurf(turf))
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.turfList.observe(this, { turfs ->
            adapter.updateTurfList(turfs)
        })
    }
}
