package com.nanda.pertemuan12tugas2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nanda.pertemuan12tugas2.Friend
import com.nanda.pertemuan12tugas2.databinding.ItemFriendBinding

class FriendAdapter(
    private val onEditClick: (Friend) -> Unit,
    private val onDeleteClick: (Friend) -> Unit
) : ListAdapter<Friend, FriendAdapter.FriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FriendViewHolder(
        private val binding: ItemFriendBinding,
        private val onEditClick: (Friend) -> Unit,
        private val onDeleteClick: (Friend) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            with(binding) {
                tvName.text = friend.name
                tvPhone.text = friend.phone
                tvEmail.text = friend.email
                if (!friend.photoUrl.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load(friend.photoUrl)
                        .circleCrop()
                        .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<android.graphics.drawable.Drawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("FriendAdapter", "Gagal memuat gambar untuk ${friend.name}: ${friend.photoUrl}, error: ${e?.message}")
                                ivPhoto.setImageDrawable(null)
                                return false
                            }

                            override fun onResourceReady(
                                resource: android.graphics.drawable.Drawable,
                                model: Any?,
                                target: Target<android.graphics.drawable.Drawable>,
                                dataSource: com.bumptech.glide.load.DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d("FriendAdapter", "Berhasil memuat gambar untuk ${friend.name}: ${friend.photoUrl}")
                                return false
                            }
                        })
                        .into(ivPhoto)
                } else {
                    Log.d("FriendAdapter", "Tidak ada URL foto untuk ${friend.name}")
                    ivPhoto.setImageDrawable(null)
                }

                // Single tap to edit
                root.setOnClickListener {
                    Log.d("FriendAdapter", "CardView diklik untuk edit: ${friend.name}")
                    onEditClick(friend)
                }

                // Long press to delete
                root.setOnLongClickListener {
                    Log.d("FriendAdapter", "CardView diklik lama untuk hapus: ${friend.name}")
                    AlertDialog.Builder(root.context)
                        .setTitle("Hapus Teman")
                        .setMessage("Apakah Anda yakin ingin menghapus ${friend.name}?")
                        .setPositiveButton("Hapus") { _, _ ->
                            Log.d("FriendAdapter", "Konfirmasi hapus untuk ${friend.name}")
                            onDeleteClick(friend)
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    true
                }
            }
        }
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}