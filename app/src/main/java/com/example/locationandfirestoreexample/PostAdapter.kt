package com.example.locationandfirestoreexample

import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.locationandfirestoreexample.databinding.RowItemBinding

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            val model = differ.currentList[adapterPosition]
            d("MODEL","$model")
            binding.apply {
                tvUserName.text = model.authorUserName
                tvStatus.text = model.text
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<PostModel>() {
        override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.onBind()

    override fun getItemCount(): Int = differ.currentList.size

}