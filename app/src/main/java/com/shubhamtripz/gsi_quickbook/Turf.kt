package com.shubhamtripz.gsi_quickbook

import android.os.Parcel
import android.os.Parcelable

data class Turf(
    val name: String = "",
    val city: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val price: Long = 0L, // Changed from String to Long
    val images: List<String> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(), // Reading Long instead of String
        parcel.createStringArrayList() ?: emptyList()
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

    companion object CREATOR : Parcelable.Creator<Turf> {
        override fun createFromParcel(parcel: Parcel): Turf {
            return Turf(parcel)
        }

        override fun newArray(size: Int): Array<Turf?> {
            return arrayOfNulls(size)
        }
    }
}
