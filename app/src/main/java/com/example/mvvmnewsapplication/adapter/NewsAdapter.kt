package com.example.mvvmnewsapplication.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.models.Article

class NewsAdapter :RecyclerView.Adapter<ArticleViewHolder>(){




    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        var article = differ.currentList[position]

        holder.apply {
            Glide.with(holder.itemView).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            holder.itemView.apply {
                setOnClickListener {
                    onItemclickListener?.let { it(article)}
                }
            }


        }

//        anther way
//        holder.itemView.apply {
//            setOnClickListener {
//                onItemclickListener?.let { it(article) }
//
//            }
//        }





    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemclickListener : ((Article)->Unit)?=null

    fun setOnItemClickListener(listener : (Article)->Unit){
        onItemclickListener = listener

    }
}

class ArticleViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    lateinit var ivArticleImage:ImageView
    lateinit var tvSource:TextView
    lateinit var tvTitle:TextView
    lateinit var tvDescription:TextView
    lateinit var tvPublishedAt:TextView

    init {
        ivArticleImage = itemView.findViewById(R.id.ivArticleImage)
        tvSource = itemView.findViewById(R.id.tvSource)
        tvTitle = itemView.findViewById(R.id.tvTitle)
        tvDescription = itemView.findViewById(R.id.tvDescription)
        tvPublishedAt = itemView.findViewById(R.id.tvPublishedAt)
    }

}