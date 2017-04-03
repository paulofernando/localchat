package site.paulo.localchat.data.local

import android.content.ContentValues
import android.database.Cursor
import site.paulo.localchat.data.model.ribot.Name
import site.paulo.localchat.data.model.ribot.Profile
import site.paulo.localchat.extension.getLong
import site.paulo.localchat.extension.getString
import java.util.*

object Db {

    object RibotProfileTable {
        val TABLE_NAME = "ribot_profile"

        val COLUMN_EMAIL = "email"
        val COLUMN_FIRST_NAME = "first_name"
        val COLUMN_LAST_NAME = "last_name"
        val COLUMN_HEX_COLOR = "hex_color"
        val COLUMN_DATE_OF_BIRTH = "date_of_birth"
        val COLUMN_AVATAR = "avatar"
        val COLUMN_BIO = "bio"

        val CREATE = """
            CREATE TABLE ${Db.RibotProfileTable.TABLE_NAME}(
              ${Db.RibotProfileTable.COLUMN_EMAIL}         TEXT PRIMARY KEY,
              ${Db.RibotProfileTable.COLUMN_FIRST_NAME}    TEXT NOT NULL,
              ${Db.RibotProfileTable.COLUMN_LAST_NAME}     TEXT NOT NULL,
              ${Db.RibotProfileTable.COLUMN_HEX_COLOR}     TEXT NOT NULL,
              ${Db.RibotProfileTable.COLUMN_DATE_OF_BIRTH} INTEGER NOT NULL,
              ${Db.RibotProfileTable.COLUMN_AVATAR}        TEXT,
              ${Db.RibotProfileTable.COLUMN_BIO}           TEXT
            )
        """

        fun toContentValues(profile: Profile): ContentValues {
            val values = ContentValues()
            values.put(Db.RibotProfileTable.COLUMN_EMAIL, profile.email)
            values.put(Db.RibotProfileTable.COLUMN_FIRST_NAME, profile.name.first)
            values.put(Db.RibotProfileTable.COLUMN_LAST_NAME, profile.name.last)
            values.put(Db.RibotProfileTable.COLUMN_HEX_COLOR, profile.hexColor)
            values.put(Db.RibotProfileTable.COLUMN_DATE_OF_BIRTH, profile.dateOfBirth.time)
            values.put(Db.RibotProfileTable.COLUMN_AVATAR, profile.avatar)
            values.put(Db.RibotProfileTable.COLUMN_BIO, profile.bio ?: "")
            return values
        }

        fun parseCursor(cursor: Cursor): Profile {
            val name = Name(cursor.getString(COLUMN_FIRST_NAME), cursor.getString(COLUMN_LAST_NAME))

            return Profile(
                    email = cursor.getString(COLUMN_EMAIL),
                    name = name,
                    hexColor = cursor.getString(COLUMN_HEX_COLOR),
                    dateOfBirth = Date(cursor.getLong(COLUMN_DATE_OF_BIRTH)),
                    avatar = cursor.getString(COLUMN_AVATAR),
                    bio = cursor.getString(COLUMN_BIO))
        }
    }
}
