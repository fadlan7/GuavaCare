package com.fadlan.guavacare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fadlan.guavacare.databinding.ItemListLeafDiseaseBinding
import com.fadlan.guavacare.model.LeafGuavaDisease
import java.util.*

class LeafGuavaDiseaseAdapter (private val guavaDiseaseLists: ArrayList<LeafGuavaDisease>): RecyclerView.Adapter<LeafGuavaDiseaseAdapter.RecyclerViewHolder>(),Filterable{

    private lateinit var onItemClickCallBack: OnItemClickCallBack
    private var filterGuavaDiseaseLists: ArrayList<LeafGuavaDisease> = guavaDiseaseLists

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ItemListLeafDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    inner class RecyclerViewHolder(private val binding: ItemListLeafDiseaseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(guavaDisease: LeafGuavaDisease) {
            Glide.with(itemView.context)
                .load(guavaDisease.diseaseImage)
                .apply(RequestOptions().override(300, 300))
                .into(binding.ivItemPicture)
            binding.tvItemName.text = guavaDisease.diseaseName
            binding.tvSubName.text = guavaDisease.diseaseSubName
            itemView.setOnClickListener { onItemClickCallBack.onItemClicked(guavaDisease) }
        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(filterGuavaDiseaseLists[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = filterGuavaDiseaseLists.size


    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val itemSearch = constraint.toString()
                filterGuavaDiseaseLists = if (itemSearch.isEmpty()){
                    guavaDiseaseLists
                }else{
                    val guavaLists = ArrayList<LeafGuavaDisease>()
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
                filterGuavaDiseaseLists= result?.values as ArrayList<LeafGuavaDisease>
                notifyDataSetChanged()
            }

        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallback
    }

    interface OnItemClickCallBack{
        fun onItemClicked(data: LeafGuavaDisease)
    }
}