package site.paulo.localchat.ui.user

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import site.paulo.localchat.R
import site.paulo.localchat.R.id.*
import site.paulo.localchat.data.model.chatgeo.User
import site.paulo.localchat.ui.utils.ctx
import javax.inject.Inject

class UsersNearbyAdapter
@Inject
constructor() : RecyclerView.Adapter<UsersNearbyAdapter.UserViewHolder>() {

    var users = emptyList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersNearbyAdapter.UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsersNearbyAdapter.UserViewHolder, position: Int) {
        holder.bindUser(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindUser(user: User) {
            with(user) {
                Picasso.with(itemView.ctx).load(profilePic)
                        .resize(itemView.ctx.resources.getDimension(R.dimen.image_width_user).toInt(),
                                itemView.ctx.resources.getDimension(R.dimen.image_height_user).toInt())
                        .centerCrop().into(itemView.image_profile_nearby)
                itemView.text_first_name_user.text = firstName
                itemView.text_email_user.text = email
                //itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}
