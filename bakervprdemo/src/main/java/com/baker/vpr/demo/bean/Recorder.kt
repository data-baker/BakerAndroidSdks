package com.baker.vpr.demo.bean

import android.os.Parcel
import android.os.Parcelable

/**
 *
 *@author xujian
 *@date 2021/11/12
 */
data class Recorder(val name: String?, val score: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recorder> {
        override fun createFromParcel(parcel: Parcel): Recorder {
            return Recorder(parcel)
        }

        override fun newArray(size: Int): Array<Recorder?> {
            return arrayOfNulls(size)
        }
    }
}
