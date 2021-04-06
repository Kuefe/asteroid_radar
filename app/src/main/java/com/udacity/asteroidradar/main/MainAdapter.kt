package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ListViewItemBinding
import com.udacity.asteroidradar.models.Asteroid


/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class MainAdapter(val onClickListener: OnClickListener) :
    ListAdapter<Asteroid, MainAdapter.AsteroidViewHolder>(DiffCallback) {

    /**
     * The AsteroidViewHolder constructor takes the binding variable from the associated
     * ListViewItem, which nicely gives it access to the full [Asteroid] information.
     */
    class AsteroidViewHolder(private var binding: ListViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {

        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(ListViewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return newItem == newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return newItem.id == oldItem.id
        }

    }


    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Asteroid]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Asteroid]
     */
    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}