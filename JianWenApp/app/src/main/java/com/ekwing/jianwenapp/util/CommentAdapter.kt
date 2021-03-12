package com.ekwing.jianwenapp.util

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.bean.CommentData
import kotlinx.android.synthetic.main.comment_item.view.*

class CommentAdapter(val CommentList : ArrayList<CommentData>, val context : Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val nickname : TextView = view.findViewById(R.id.comment_item_nickname)
        val photo : ImageView = view.findViewById(R.id.comment_item_photo)
        val words : TextView = view.findViewById(R.id.comment_item_words)
        val time : TextView = view.findViewById(R.id.comment_item_time)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = CommentList[position]
        holder.nickname.setText(comment.nickname)
        holder.words.setText(comment.words)
        Glide.with(context).load(DataUtil.UPLOAD + comment.userid + ".jpg").into(holder.photo)
        holder.time.setText(comment.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return CommentList.size
    }

}