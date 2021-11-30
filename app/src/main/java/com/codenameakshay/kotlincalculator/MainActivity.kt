package com.codenameakshay.kotlincalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun numberAction(view: android.view.View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    WorkingTV.append(view.text)
                    canAddOperation = false
                }
                canAddDecimal = false
            } else {
                WorkingTV.append(view.text)
                canAddOperation = true
            }
        }
    }

    fun operationAction(view: android.view.View) {
        if (view is Button && canAddOperation) {
            WorkingTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: android.view.View) {
        WorkingTV.text = ""
        ResultsTV.text = ""
    }

    fun backSpaceAction(view: android.view.View) {
        val length = WorkingTV.length()
        if (length > 0)
            WorkingTV.text = WorkingTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: android.view.View) {
        ResultsTV.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        Log.d("RESULT",digitsOperators.toString())

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""
        Log.d("RESULT",timesDivision.toString())

        val result = addSubCalculate(timesDivision)
        Log.d("RESULT",result.toString())
        return result.toString()
    }

    private fun addSubCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {

        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()

        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val previousDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(previousDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(previousDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(previousDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartIndex)
                newList.add(passedList[i])
        }
        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in WorkingTV.text) {
            if (character.isDigit() || character.equals('.')) {
                currentDigit += character
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())
        return list
    }
}