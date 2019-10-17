package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var username: String
) {
    @PrimaryKey
    var key = 1

    fun getFullName() = when (firstName != "" || lastName != "") {
        true -> String.format("%s %s", firstName, lastName)
        false -> "Attendee"
    }
}
