package site.paulo.localchat.ui.user

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.view.*
import kotlinx.android.synthetic.main.item_user.view.*
import site.paulo.localchat.R
import site.paulo.localchat.data.model.firebase.User
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

                if(!pic.equals(""))
                    Picasso.with(itemView.ctx).load(pic)
                        .resize(itemView.ctx.resources.getDimension(R.dimen.image_width_user).toInt(),
                            itemView.ctx.resources.getDimension(R.dimen.image_height_user).toInt())
                        .centerCrop().into(itemView.profileNearbyUserImg)
                else
                    Picasso.with(itemView.ctx).load(R.drawable.nearby_user_default)
                        .resize(itemView.ctx.resources.getDimension(R.dimen.image_width_user).toInt(),
                            itemView.ctx.resources.getDimension(R.dimen.image_height_user).toInt())
                        .centerCrop().into(itemView.profileNearbyUserImg)

                itemView.firstNameUserTv.text = name
                itemView.emailUserTv.text = email
                //itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}
