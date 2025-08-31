package com.hfad.agendax

import android.util.Log
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.agendax.databinding.WeekCellBinding
import com.hfad.agendax.databinding.WeeksBinding
import com.hfad.agendax.util.*
import com.hfad.agendax.vo.Month
import com.hfad.agendax.vo.WeekCellModel
import java.util.*
import java.util.concurrent.TimeUnit

class WeeksAdapter: RecyclerView.Adapter<WeeksAdapter.WeeksHolder>() {

    // Listeners
    private var firstDateListener: FirstDate? = null
    private var dateSelectedListener: DateSelected? = null
    private var currentPage: Int = 0
    private var recyclerView: RecyclerView? = null
    private var weeks = mutableListOf<MutableList<Calendar?>>()
    private var selectedDate = ObservableField(Calendar.getInstance())
    var currentDate: Calendar = Calendar.getInstance()
        private set
    private var currentMonth: Month = Month(0,0)
        set(value) {
            field = value
        }


    interface FirstDate {
        fun onFirstDateChange(date: Calendar)
    }

    interface DateSelected {
        fun onDateSelected(date: Calendar)
    }


    fun setFirstDateListener(listener: FirstDate){
        this.firstDateListener = listener
    }

    fun removeFirstDateListener(){
        this.firstDateListener = null
    }

    fun setDateSelectListener(listener: DateSelected){
        this.dateSelectedListener = listener
    }

    fun removeDateSelectListener(){
        this.dateSelectedListener = null
    }

    fun changeCurrentDate(calendar: Calendar){
        currentDate = DateTime.stripTime(calendar)
        this.notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView

        this.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                currentPage = layoutManager.findFirstVisibleItemPosition()
                val date = weeks[currentPage].filterNotNull().first()
                firstDateListener?.onFirstDateChange(DateTime.stripTime(date.clone() as Calendar))

                //For adding month to the end of the list
                if((currentMonth.month + 1) % 12  == date.get(Calendar.MONTH)){
                    val endDate = weeks.last().filterNotNull().last().clone() as Calendar
                    val startDate = weeks.first().filterNotNull().first().clone() as Calendar
                    endDate.add(Calendar.DATE, 1)
                    weeks += generateMonth(endDate)
                    val n = startDate.getActualMaximum(Calendar.WEEK_OF_MONTH)
                    weeks.removeRange(0, n)
                    recyclerView.adapter?.notifyItemRangeRemoved(0, n)
                }

                //For adding month to the front of the list
                if(currentMonth.month == (date.get(Calendar.MONTH) + 1) % 12 ){
                    val endDate = weeks.last().filterNotNull().last().clone() as Calendar
                    val startDate = weeks.first().filterNotNull().first().clone() as Calendar
                    startDate.add(Calendar.DATE, -1)
                    weeks = (generateMonth(startDate) + weeks) as MutableList<MutableList<Calendar?>>
                    val n = endDate.getActualMaximum(Calendar.WEEK_OF_MONTH)
                    val m = startDate.getActualMaximum(Calendar.WEEK_OF_MONTH)
                    weeks.removeRange(weeks.size-n, n)
                    recyclerView.adapter?.notifyItemRangeInserted(0, m)

                }
                currentMonth = Month(date.get(Calendar.MONTH), date.get(Calendar.YEAR))

            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
        })

        setCurrentDate(currentDate, false)
    }


    class WeeksHolder(val binding: WeeksBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun create(parent: ViewGroup): WeeksHolder {
                val view = WeeksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return WeeksHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeksHolder {
        return WeeksHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WeeksHolder, position: Int) {
        holder.binding.weekHolder.removeAllViews()
        val item = weeks[position]

        item.forEach { c ->
            var weekCellModel: WeekCellModel? = null

            if(c != null) {
                weekCellModel = WeekCellModel(c.get(Calendar.DATE), getDayWord(c.get(Calendar.DAY_OF_WEEK)),c)
            }

            val weekCell = WeekCellBinding.inflate(LayoutInflater.from(holder.binding.root.context), null,false)
            weekCell.model = weekCellModel


            // Setting formats
            if(c!= null && c.equals(DateTime.stripTime(currentDate))){
                weekCell.currentDate = true
            }
            weekCell.selectedDate = selectedDate

            weekCell.callback = object: DateSelected{
                override fun onDateSelected(date: Calendar) {
                    selectedDate.set(date)
                    dateSelectedListener?.onDateSelected(DateTime.stripTime(date.clone() as Calendar))
                }
            }

            val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            weekCell.weekCell.layoutParams = layoutParams
            holder.binding.weekHolder.addView(weekCell.root)


        }



    }

    override fun getItemCount(): Int {
        return weeks.size
    }

    fun setSelectedDate(date: Calendar){
        selectedDate.set(DateTime.stripTime(date))

        setCurrentDate(date, false)
    }



    private fun setCurrentDate(date: Calendar, smoothScroll: Boolean = true){
        if(currentMonth.month != date.get(Calendar.MONTH) || currentMonth.year != date.get(Calendar.YEAR)) {
            val indexDate = date.clone() as Calendar
            indexDate.add(Calendar.MONTH, -1)
            weeks = generateMonth(indexDate)
            indexDate.add(Calendar.MONTH, 1)

            currentMonth = Month(indexDate.get(Calendar.MONTH), indexDate.get(Calendar.YEAR))
//            firstDateListener?.onFirstDateChange(date)

            weeks += generateMonth(indexDate)
            indexDate.add(Calendar.MONTH, 1)
            weeks += generateMonth(indexDate)
            this.notifyDataSetChanged()
        }
        setPosition(date, smoothScroll)

    }

    private fun setPosition(date: Calendar, smoothScroll: Boolean){
        var firstDate = weeks.first().filterNotNull()[0].clone() as Calendar

        var pos = 0

        pos += firstDate.getActualMaximum(Calendar.WEEK_OF_MONTH)
        firstDate.add(Calendar.MONTH, 1)

        pos += date.get(Calendar.WEEK_OF_MONTH) - firstDate.get(Calendar.WEEK_OF_MONTH)


        firstDate = weeks[pos].filterNotNull()[0].clone() as Calendar
        firstDateListener?.onFirstDateChange(firstDate)

        if(smoothScroll)
            recyclerView?.smoothScrollToPosition(pos)
        else
            recyclerView?.scrollToPosition(pos)

    }

    private fun generateMonth(date: Calendar): MutableList<MutableList<Calendar?>> {
        var startDate = date.clone() as Calendar
        startDate.set(Calendar.DAY_OF_MONTH, 1)
        var dates = mutableListOf<Calendar?>()
        val weeks = mutableListOf<MutableList<Calendar?>>()

        // padding initial date values with null
        for(i in 1..(startDate.get(Calendar.DAY_OF_WEEK) - startDate.firstDayOfWeek)){
            dates.add(null)
        }

        val month = startDate.get(Calendar.MONTH)

        while(startDate.get(Calendar.MONTH) == month){
            dates.add(DateTime.stripTime(startDate))
            if(dates.size % 7 == 0){
                weeks.add(dates)
                dates = mutableListOf()
            }

            startDate  = startDate.clone() as Calendar
            startDate.add(Calendar.DATE, 1)
        }

        // padding the remaining values with zero
        if(dates.size != 0){
            for(i in dates.size until 7){
                dates.add(null)
            }
            weeks.add(dates)
        }

        return weeks
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

}