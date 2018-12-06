package org.hackillinois.android.model

import android.os.Parcel
import android.os.Parcelable

data class Event(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(name)
            writeString(description)
            writeLong(startTime)
            writeLong(endTime)
            writeString(locationDescription)
            writeDouble(latitude)
            writeDouble(longitude)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
