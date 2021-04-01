package com.alfanthariq.mvvm_moviedb.utils

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.View
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.widget.DatePicker

object DateOperationUtil {

    fun getCurrentTimeStr(formatStr : String) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val cal = Calendar.getInstance()
        val tgl = format.format(cal.time)

        return tgl
    }

    fun dateAdd(date : Date, day : Int) : Date {
        val gc1 = GregorianCalendar()
        gc1.time = date
        gc1.add(Calendar.DATE, day)

        return gc1.time
    }

    fun strToDate(formatStr : String, str : String) : Date? {
        val dateFormat = SimpleDateFormat(formatStr, Locale.US)
        val convertedDate: Date
        try {
            convertedDate = dateFormat.parse(str)
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun getTimeStr(formatStr : String, date : Date) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val tgl = format.format(date)

        return tgl
    }

    fun dateStrFormat(formatInput : String, formatOutput : String, date : String) : String {
        val dateFormat = SimpleDateFormat(formatInput, Locale.US)
        val convertedDate: Date
        try {
            convertedDate = dateFormat.parse(date)
            val newformat = SimpleDateFormat(formatOutput, Locale.US)
            val finalDateString = newformat.format(convertedDate)
            return finalDateString
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    fun openDatePicker(context: Context, listener : DatePickerDialog.OnDateSetListener) : DatePickerDialog {
        val today = Calendar.getInstance()

        return DatePickerDialog(context, listener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
    }

    fun createDialogWithoutDateField(context: Context, listener : DatePickerDialog.OnDateSetListener): DatePickerDialog {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(context, listener, mYear, mMonth, mDay)
        try {
            val datePickerDialogFields = dpd.javaClass.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker = datePickerDialogField.get(dpd) as DatePicker
                    val datePickerFields = datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        Log.i("test", datePickerField.name)
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField.get(datePicker)
                            (dayPicker as View).visibility = View.GONE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }

        return dpd
    }

    fun dateToMilliseconds(date: String): Long {
        //String date_ = date;
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            val mDate = sdf.parse(date)
            val timeInMilliseconds = mDate.time
            println("Date in milli :: $timeInMilliseconds")
            return timeInMilliseconds
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return 0
    }
}