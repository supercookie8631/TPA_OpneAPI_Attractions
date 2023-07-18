package com.example.attractions.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.attractions.R
import com.example.attractions.domain.AttractionsDataItem

class AttractionsAdapter : RecyclerView.Adapter<AttractionsAdapter.InnerHolder>() {
    private val mContentList = arrayListOf<AttractionsDataItem>()

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): InnerHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_item_attractions, parent, false)
        return InnerHolder(itemView)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.apply {
            val itemCardView = findViewById<CardView>(R.id.itemCardView)
            val itemTitleTv = findViewById<TextView>(R.id.itemTitleTv)
            val itemIntroductionTv = findViewById<TextView>(R.id.itemIntroductionTv)
            val itemImageView = findViewById<ImageView>(R.id.itemImageView)

            with(mContentList[position])
            {
                itemTitleTv.text = name
                itemIntroductionTv.text = introduction
                if (images.isNotEmpty() && images[0].src != "") {
                    Glide.with(context)
                        .load(images[0].src)
                        .override(itemImageView.width, itemImageView.height)
                        .placeholder(R.drawable.loading_icon)
                        .fitCenter()
                        .into(itemImageView)
                } else {
                    Glide.with(context)
                        .load(R.drawable.noresult)
                        .into(itemImageView)
                }
                itemCardView.setOnClickListener {
                    ViewCompat.setTransitionName(itemImageView, "itemImageView$position")
                    println(ViewCompat.getTransitionName(itemImageView))
                    val imagePair = Pair<View, String>(itemImageView, "itemImageView$position")
                    val extras =
                        FragmentNavigatorExtras(imagePair)
                    //數據
                    val bundle = Bundle().apply {
                        putInt("position", position)
                        if (images.isNotEmpty() && images[0].src != "") {
                            val imageUrlList = ArrayList<String>()
                            for (item in images) {
                                imageUrlList.add(item.src)
                            }
                            putStringArrayList("imageUrlList", imageUrlList)
                        } else {
                            putStringArrayList("imageUrlList", ArrayList())
                        }
                    }
                    //跳轉到Detail頁面
                    itemCardView.findNavController().navigate(
                        R.id.action_mainFragment_to_detailFragment,
                        bundle,
                        null,
                        extras
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mContentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(it: List<AttractionsDataItem>) {
        mContentList.clear()
        mContentList.addAll(it)
        notifyDataSetChanged()
    }
}