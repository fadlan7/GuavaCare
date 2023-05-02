package com.fadlan.guavacare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fadlan.guavacare.databinding.ItemListGuavaDiseaseBinding
import com.fadlan.guavacare.databinding.RvPredictionGuavaResultBinding
import com.fadlan.guavacare.model.Detection
import com.fadlan.guavacare.model.GuavaDisease
import java.util.*

class GuavaDiseaseAdapter (private val guavaDiseaseLists: ArrayList<GuavaDisease>): RecyclerView.Adapter<GuavaDiseaseAdapter.RecyclerViewHolder>(),Filterable{

    private lateinit var onItemClickCallBack: OnItemClickCallBack
    private var filterGuavaDiseaseLists: ArrayList<GuavaDisease> = guavaDiseaseLists

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_guava_disease, parent,false)
//        return GridViewHolder(view)

        val binding = ItemListGuavaDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    inner class RecyclerViewHolder(private val binding: ItemListGuavaDiseaseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(guavaDisease: GuavaDisease) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(guavaDisease.diseaseImage)
                    .apply(RequestOptions().override(300, 300))
                    .into(binding.ivItemPicture)
                binding.tvItemName.text = guavaDisease.diseaseName
                itemView.setOnClickListener { onItemClickCallBack.onItemClicked(guavaDisease) }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bind(filterGuavaDiseaseLists[position])

    override fun getItemCount(): Int = filterGuavaDiseaseLists.size


    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val itemSearch = constraint.toString()
                filterGuavaDiseaseLists = if (itemSearch.isEmpty()){
                    guavaDiseaseLists
                }else{
                    val guavaLists = ArrayList<GuavaDisease>()
                    for (item in guavaDiseaseLists){
                        if (item.diseaseName?.toLowerCase(Locale.ROOT)?.contains(itemSearch.toLowerCase(
                                Locale.ROOT))!!) {
                            guavaLists.add(item)
                        }
                    }
                    guavaLists
                }
                var filterResults = FilterResults()
                filterResults.values = filterGuavaDiseaseLists
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, result: FilterResults?) {
                filterGuavaDiseaseLists= result?.values as ArrayList<GuavaDisease>
                notifyDataSetChanged()
            }

        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallback
    }

    interface OnItemClickCallBack{
        fun onItemClicked(data: GuavaDisease)
    }
}