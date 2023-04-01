package com.fadlan.guavacare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fadlan.guavacare.R
import com.fadlan.guavacare.databinding.RvPredictionResultBinding
import com.fadlan.guavacare.model.Detection
//import kotlinx.android.sy

class PredictAdapter(private val resultDetection: ArrayList<Detection>)  : RecyclerView.Adapter<PredictAdapter.RecyclerViewHolder>(){
    private lateinit var onItemClickDetail: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickDetail = onItemClickCallBack
    }

    fun setResultDetectionData(items: ArrayList<Detection>) {
        resultDetection.clear()
        items.sortByDescending { it.accuracy }
        resultDetection.addAll(items)
        notifyDataSetChanged()
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//        val view: View = LayoutInflater
//            .from(parent.context)
//            .inflate(R.layout.item_guava_disease, parent, false)
//        return RecyclerViewHolder(view)
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = RvPredictionResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        val limit = 1
        return if (resultDetection.size > limit) limit
        else resultDetection.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) =
        holder.bind(resultDetection[position])

    inner class RecyclerViewHolder(private val binding: RvPredictionResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Detection) {
            binding.apply {
                tvNameResult.text = "${data.name}"
                tvAccResult.text = "Probability: ${data.accuracy}%"
                itemView.setOnClickListener { onItemClickDetail.onItemClicked(data) }
            }
//            with(itemView) {
//                tv_name_result.text = "Name of Disease: ${detection.name}"
//                //val acc = "Probability: ${detection.accuracy*100}%"
//                tv_acc_result.text = "Probability: ${detection.accuracy}%"
//                itemView.setOnClickListener { onItemClickDetail.onItemClicked(detection) }
//            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Detection)
    }
}