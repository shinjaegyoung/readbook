package com.example.readbook.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.R
import kotlinx.android.synthetic.main.activity_calendar_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class  CalendarAdapter(val context: Context): RecyclerView.Adapter<CalendarAdapter.ItemView>(){
    private val array = ArrayList<Long>()
    private var month = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return  ItemView(view)
    }

    override fun onBindViewHolder(holder: ItemView, position: Int) {
        //holder.stamp.visibility=View.VISIBLE

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = array.get(position)
        val month = calendar.get(Calendar.MONTH)
        if (this.month != month){
            holder.background.setBackgroundColor(Color.GRAY)
        } else {
            holder.background.setBackgroundColor(Color.WHITE)
        }
        holder.textDay.setText(SimpleDateFormat("dd").format(calendar.time))

        holder.itemView.setOnClickListener{

            if (!holder.stamp.isVisible){
                // 1을 저장하는 코드를 넣어주고

                holder.stamp.setVisibility(View.VISIBLE)
                Toast.makeText(
                    context,
                    SimpleDateFormat("참 잘했어요").format(calendar.time),
                    Toast.LENGTH_SHORT
                ).show() // 클릭을 햇을시 해당 년월일 표시해줌.show()


            }else if(holder.stamp.isVisible){
                // 0을 저장하는 코드를 넣어주고
                holder.stamp.setVisibility(View.INVISIBLE)


            }

            /*val intent = Intent(context, CalendarDetailActivity::class.java)
            context.startActivity(intent)*/



        }
    }

    override fun getItemCount(): Int {
        return array.size // 아이템 갯수
    }

    fun setList(array: ArrayList<Long>, month:Int){
        this.month = month
        this.array.clear()
        this.array.addAll(array)
        notifyDataSetChanged()
    }

    inner class ItemView(view: View) : RecyclerView.ViewHolder(view){
        val stamp : ImageView = view.findViewById(R.id.imageview)
        val textDay: TextView = view.findViewById(R.id.text_day)
        val background : ConstraintLayout =view.findViewById(R.id.background)
    }

}