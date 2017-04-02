package site.paulo.localchat.ui.user

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import site.paulo.localchat.R
import site.paulo.localchat.data.model.chatgeo.User
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
        val user = users[position]

        holder.firstNameTextView.text = user.first_name
        holder.emailTextView.text = user.email
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.view_hex_color)
        lateinit var hexColorView: View

        @BindView(R.id.text_first_name_user)
        lateinit var firstNameTextView: TextView

        @BindView(R.id.text_email_user)
        lateinit var emailTextView: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
