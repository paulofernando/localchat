package site.paulo.localchat.ui.dashboard

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk

class TestUtils {

    object FirebaseUserTestConstants {
        val UID = "abc"
        val EMAIL = "test@test.com"
        val NAME = "Abc De"
        //val PHOTO_URI: Uri = Uri.parse("http://test/path")
    }

    companion object {
        fun getMockFirebaseUser(): FirebaseUser {
            val user: FirebaseUser = mockk()
            every { user.uid } returns FirebaseUserTestConstants.UID
            every { user.email } returns FirebaseUserTestConstants.EMAIL
            every { user.displayName } returns FirebaseUserTestConstants.NAME
            //every { user.photoUrl } returns FirebaseUserTestConstants.PHOTO_URI

            return user
        }
    }
}