package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.TrackerContainer
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.repository.rolesRepository
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerViewModel : ViewModel() {
    var lastScanStatus: MutableLiveData<ScanStatus> = MutableLiveData()
    lateinit var roles: LiveData<Roles>

    private val CHECK_IN_NAME = "Check-in"

    private lateinit var eventName: String

    fun init(eventName: String) {
        this.eventName = eventName
        this.roles = rolesRepository.fetch()
    }

    fun checkUserIntoEvent(eventId: String, userId: String, staffOverride: Boolean) {
        if (eventName == CHECK_IN_NAME) {
            val checkIn = CheckIn(userId, staffOverride, hasCheckedIn = true, hasPickedUpSwag = true)
            checkInUser(checkIn)
        } else {
            val userEventPair = UserEventPair(eventId, userId)
            markUserAsAttendingEvent(userEventPair)
        }
    }

    fun checkInUser(checkIn: CheckIn) {
        App.getAPI().checkInUser(checkIn).enqueue(object : Callback<CheckIn> {
            override fun onFailure(call: Call<CheckIn>, t: Throwable) {
                var scanStatus = ScanStatus(false, "", "Request could not be made. Please try again.")
                lastScanStatus.postValue(scanStatus)
            }

            override fun onResponse(call: Call<CheckIn>, response: Response<CheckIn>) {
                if (response.isSuccessful) {
                    // Check-in was a success
                    var userId = response.body()?.id.toString()
                    var scanStatus = ScanStatus(true, userId, "")
                    lastScanStatus.postValue(scanStatus)
                } else {
                    val error = JSONObject(response.errorBody()?.string())
                    val errorType = error.getString("type")
                    val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                        error.getString("message")
                    } else {
                        "Internal API error"
                    }
                    var scanStatus = ScanStatus(false, "", errorMessage)
                    lastScanStatus.postValue(scanStatus)
                }
            }
        })
    }

    fun markUserAsAttendingEvent(userEventPair: UserEventPair) {
        App.getAPI().markUserAsAttendingEvent(userEventPair).enqueue(object : Callback<TrackerContainer> {
            override fun onFailure(call: Call<TrackerContainer>, t: Throwable) {
                var scanStatus = ScanStatus(false, "", "Request could not be made. Please try again.")
                lastScanStatus.postValue(scanStatus)
            }

            override fun onResponse(call: Call<TrackerContainer>, response: Response<TrackerContainer>) {
                if (response.isSuccessful) {
                    // User marked as attending the event
                    var userId = response.body()?.userTracker?.userId.toString()
                    var scanStatus = ScanStatus(true, userId, "")
                    lastScanStatus.postValue(scanStatus)
                } else {
                    val error = JSONObject(response.errorBody()?.string())
                    val errorType = error.getString("type")
                    val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                        error.getString("message")
                    } else {
                        "Internal API error"
                    }
                    var scanStatus = ScanStatus(false, "", errorMessage)
                    lastScanStatus.postValue(scanStatus)
                }
            }
        })
    }
}