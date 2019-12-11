/*
 * Copyright 2017 Paulo Fernando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.paulo.localchat.ui.dashboard.nearby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_user.view.*
import org.jetbrains.anko.startActivity
import site.paulo.localchat.R
import site.paulo.localchat.data.manager.CurrentUserManager
import site.paulo.localchat.data.model.firebase.NearbyUser
import site.paulo.localchat.ui.room.RoomActivity
import site.paulo.localchat.ui.utils.Utils
import site.paulo.localchat.ui.utils.ctx
import site.paulo.localchat.ui.utils.getFirebaseId
import site.paulo.localchat.ui.utils.loadResourceAndResize
import site.paulo.localchat.ui.utils.loadUrlAndResize
import javax.inject.Inject

class UsersNearbyAdapter
@Inject
constructor() : androidx.recyclerview.widget.RecyclerView.Adapter<UsersNearbyAdapter.UserViewHolder>() {

    var nearbyUsers = mutableListOf<NearbyUser>()

    @Inject
    lateinit var currentUserManager: CurrentUserManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(nearbyUsers[position])
    }

    override fun getItemCount(): Int {
        return nearbyUsers.size
    }

    inner class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bindUser(nearbyUser: NearbyUser) {
            with(nearbyUser) {

                if(pic != "")
                    itemView.profileNearbyUserImg.loadUrlAndResize(pic,
                        itemView.ctx.resources.getDimension(R.dimen.image_width_user).toInt())
                else
                    itemView.profileNearbyUserImg.loadResourceAndResize(R.drawable.nearby_user_default,
                        itemView.ctx.resources.getDimension(R.dimen.image_width_user).toInt())

                itemView.firstNameUserTv.text = name + ", " + age.toString()
                //itemView.ageUserTv.text = age.toString()

                itemView.setOnClickListener {
                    val chatId:String = currentUserManager.getUser().chats.get(Utils.getFirebaseId(email)) ?: ""
                    itemView.ctx.startActivity<RoomActivity>("chatId" to chatId, "otherUser" to nearbyUser)
                }
            }
        }
    }
}
