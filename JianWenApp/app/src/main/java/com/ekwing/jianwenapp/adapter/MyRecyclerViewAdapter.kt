package com.ekwing.jianwenapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekwing.jianwenapp.R
import com.ekwing.jianwenapp.activity.Main2Activity
import com.ekwing.jianwenapp.bean.NewData
import kotlinx.android.synthetic.main.item_rv_list.view.*

class MyRecyclerViewAdapter (private val context: Context, private val alist:ArrayList<NewData.ResultBean.DataBean>, val userid : String, val nickname : String):RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_list,parent,false) )
    }

    override fun getItemCount(): Int {
       return alist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder?.itemView!!){
            tv_show.setText(alist[position].title)
            Glide.with(context).load(alist[position].thumbnail_pic_s).into(imgv_show)
            Log.d("debug Imgae url", alist[position].thumbnail_pic_s.toString())
            holder.itemView.setOnClickListener {
                val intent =Intent(context,Main2Activity::class.java)
                intent.putExtra("title", alist[position].title)
                intent.putExtra("image", alist[position].thumbnail_pic_s)
                intent.putExtra("url", alist[position].url)
                intent.putExtra("uniquekey", alist[position].uniquekey)
                intent.putExtra("userid", userid)
                intent.putExtra("nickname", nickname)
                context.startActivity(intent)
            }
        }

    }
}