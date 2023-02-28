package com.tomerpacific.jetpackcomposetabs

import android.app.Application
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import kotlin.random.Random

//class MainViewModel(application: Application) : AndroidViewModel(application) {
class MainViewModel : ViewModel() {

    private val _tabIndex: MutableLiveData<Int> = MutableLiveData(0)
    val tabIndex: LiveData<Int> = _tabIndex
    val tabs = listOf("Home", "About", "Settings")

    val aboutScreenText = listOf("Chris Athanas", "realityexpander", "github.com/realityexpander")

    // using a regular list (is observable)
    val _elements0 = mutableStateOf(aboutScreenText)
    var elements0: State<List<String>> = _elements0

    // using a mutable list (is not observable)
    //val _elements1 = mutableStateOf(mutableListOf("Chris Athanas", "realityexpander", "github.com/realityexpander"))
    val _elements1 = mutableStateOf(aboutScreenText.toMutableList()) // note warning
    var elements1: State<List<String>> = _elements1

    // using mutableStateListOf (is observable)
    //val _elements2 = mutableStateListOf("Chris Athanas", "realityexpander", "github.com/realityexpander")
    val _elements2 = mutableStateListOf(*aboutScreenText.toTypedArray())
    var elements2: List<String> = _elements2 // note: no State wrapper

    // convert list to vararg

    var isSwipeToTheLeft: Boolean = false
    private val draggableState = DraggableState { delta ->
        isSwipeToTheLeft = delta > 0
    }

    private val _dragState = MutableLiveData<DraggableState>(draggableState)
    val dragState: LiveData<DraggableState> = _dragState

    fun updateTabIndexBasedOnSwipe() {
        _tabIndex.value = when (isSwipeToTheLeft) {
            true -> Math.floorMod(_tabIndex.value!!.plus(1), tabs.size)
            false -> Math.floorMod(_tabIndex.value!!.minus(1), tabs.size)
        }
    }

    fun updateTabIndex(i: Int) {
        _tabIndex.value = i
    }

    fun addElement() {
        val elementToAdd = "New Element ${Random.nextInt()}"

        // using regular State & List  (long way)
         // _elements0.value.plus(elementToAdd) // does not recompose the UI
         _elements0.value = _elements0.value.plus(elementToAdd)
        println("Last item in _elements ${_elements0.value.last()}")

        // using mutable list (cleaner way but doesn't update UI!!!)
        _elements1.value.add(elementToAdd) // must use ".value" to access the list
        println("Last item in _elements ${_elements1.value.last()}")

        // using mutableStateListOf (cleanest way)
        _elements2.add(elementToAdd)  // does NOT need ".value" to access the list
        println("Last item in _elements2 ${_elements2.last()}")
    }

}