package site.paulo.localchat.data.model.ribot

import nz.bradcampbell.paperparcel.PaperParcel
import java.util.Date

@PaperParcel
data class Profile(
        val name: Name,
        val email: String,
        val hexColor: String,
        val dateOfBirth: Date,
        val bio: String?,
        val avatar: String?)
