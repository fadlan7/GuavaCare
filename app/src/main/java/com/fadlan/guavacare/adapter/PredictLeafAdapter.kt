package com.fadlan.guavacare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fadlan.guavacare.databinding.RvPredictionGuavaLeafResultBinding
import com.fadlan.guavacare.model.Detection

class PredictLeafAdapter(private val resultDetection: ArrayList<Detection>)  : RecyclerView.Adapter<PredictLeafAdapter.RecyclerViewHolder>(){
    private lateinit var onItemClickDetail: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallBack: PredictAdapter.OnItemClickCallBack) {
        this.onItemClickDetail = onItemClickCallBack
    }

    fun setResultDetectionData(items: ArrayList<Detection>) {
        resultDetection.clear()
        items.sortByDescending { it.accuracy }
        resultDetection.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = RvPredictionGuavaLeafResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        val limit = 1
        return if (resultDetection.size > limit) limit
        else resultDetection.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) =
        holder.bind(resultDetection[position])

    inner class RecyclerViewHolder(private val binding: RvPredictionGuavaLeafResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Detection) {
            binding.apply {
                tvNameResult.text = "${data.name}"
                tvAccResult.text = "${data.accuracy}%"
                itemView.setOnClickListener { onItemClickDetail.onItemClicked(data) }
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Detection)
    }
}