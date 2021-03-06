package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import org.hackillinois.android.API
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.auth.Code
import org.hackillinois.android.model.auth.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val redirectUri: String = "https://hackillinois.org/auth/?isAndroid=1"
    private val authUriTemplate: String = "https://api.hackillinois.org/auth/%s/?redirect_uri=%s"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        attendeeLoginBtn.setOnClickListener {
            redirectToOAuthProvider("github")
        }

        staffLoginBtn.setOnClickListener {
            redirectToOAuthProvider("google")
        }

        guestLoginBtn.setOnClickListener {
            launchMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = intent ?: return
        intent.action ?: return

        val uri = intent.data ?: return

        val code = uri.getQueryParameter("code") ?: return

        finishLogin(code)
    }

    private fun finishLogin(code: String) {
        // TODO: update to Retrofit 2.6.0 and use suspend functions to remove nested callbacks
        var api = App.getAPI()
        api.getJWT(getOAuthProvider(), redirectUri, Code(code)).enqueue(object : Callback<JWT> {
            override fun onFailure(call: Call<JWT>, t: Throwable) {
                showFailedToLoginStaff()
            }

            override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                response.body()?.token?.let {
                    api = App.getAPI(it)
                    thread {
                        try {
                            api.updateNotificationTopics().execute()
                        } catch (e: SocketTimeoutException) {
                            Log.e("LoginActivity", "Notifications update timed out!")
                        }
                    }

                    if (getOAuthProvider() == "google") {
                        verifyRole(api, it, "Staff")
                    } else {
                        verifyRole(api, it, "Attendee")
                    }
                }
            }
        })
    }

    private fun verifyRole(api: API, jwt: String, role: String) {
        api.roles().enqueue(object : Callback<Roles> {
            override fun onFailure(call: Call<Roles>, t: Throwable) {
                showFailedToLoginStaff()
            }

            override fun onResponse(call: Call<Roles>, response: Response<Roles>) {
                if (response.isSuccessful &&
                        response.body()?.roles?.contains(role) == true) {
                    JWTUtilities.writeJWT(applicationContext, jwt)
                    launchMainActivity()
                } else {
                    if (role == "Staff") {
                        showFailedToLoginStaff()
                    } else {
                        showFailedToLoginAttendee()
                    }
                }
            }
        })
    }

    private fun showFailedToLoginStaff() {
        Snackbar.make(findViewById(android.R.id.content), "You must have a valid staff account" +
                " to log in.", Snackbar.LENGTH_SHORT).show()
    }

    private fun showFailedToLoginAttendee() {
        Snackbar.make(findViewById(android.R.id.content), "You must RSVP to log in.", Snackbar.LENGTH_SHORT).show()
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun redirectToOAuthProvider(provider: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(authUriTemplate.format(provider, redirectUri))
        setOAuthProvider(provider)
        startActivity(intent)
    }

    private fun setOAuthProvider(provider: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider)
        editor.apply()
    }

    fun getOAuthProvider(): String {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")
                ?: ""
    }
}