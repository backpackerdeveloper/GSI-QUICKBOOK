package com.shubhamtripz.gsi_quickbook

import android.os.Parcel
import android.os.Parcelable

data class ParcelableTurf(
    val name: String,
    val city: String,
    val openTime: String,
    val closeTime: String,
    val price: Long, // Changed from String to Long
    val images: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(), // Reading Long instead of String
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(city)
        parcel.writeString(openTime)
        parcel.writeString(closeTime)
        parcel.writeLong(price) // Writing Long instead of String
        parcel.writeStringList(images)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParcelableTurf> {
        override fun createFromParcel(parcel: Parcel): ParcelableTurf {
            return ParcelableTurf(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableTurf?> {
            return arrayOfNulls(size)
        }

        fun fromTurf(turf: Turf): ParcelableTurf {
            return ParcelableTurf(
                turf.name,
                turf.city,
                turf.openTime,
                turf.closeTime,
                turf.price,
                turf.images
            )
        }
    }
}
