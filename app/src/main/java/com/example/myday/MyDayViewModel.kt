package com.example.myday

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class MyDayUiState(
    val currentYear: DiaryYear? = null, //Aquí guardamos el año actual seleccionado
    val currentMonth: DiaryMonth? = null, //Aquí guardamos el mes actual seleccionado

    val years: List<DiaryYear> = emptyList(), //Aquí guardamos todos los años en una lista
    val months: List<DiaryMonth> = emptyList(), //Hacemos lo mismo pero con los meses
    val entries: List<DiaryEntry> = emptyList(), //Y con los días o las entradas


    val isShowingYearList: Boolean = true, //Sirve para mostrar la lista de los años al inicio de la aplicación
    val isShowingMonthList: Boolean = false
)

class MyDayViewModel : ViewModel() {
    //Se guarda el estado de la aplicación en un MutableStateFlow
    private val _uiState = MutableStateFlow(MyDayUiState())
    //Se convierte el MutableStateFlow en un StateFlow, expone el estado para que la UI pueda leerlo
    val uiState: StateFlow<MyDayUiState> = _uiState

    //Se crea una "Base de Datos" en memoria
    //Como se trata de un prototipo, decidimos guardar los datos temporalmente mientras la app está abierta
    private val allYears = mutableListOf<DiaryYear>()
    private val allMonths = mutableListOf<DiaryMonth>()
    private val allEntries = mutableListOf<DiaryEntry>()

    private fun updateScreenData() {
        _uiState.update { state ->
            state.copy(
                years = allYears.sortedBy { it.title }, //Ordena los años alfabéticamente
                months = allMonths.filter { it.yearId == state.currentYear?.id } //Solo muestra los meses del año seleccionado
                    .sortedBy { it.chronologicalOrder }, //Ordena los meses cronológicamente
                entries = allEntries.filter { it.monthId == state.currentMonth?.id } //Solo muestra las entradas del mes seleccionado
            )
        }
    }

    // --- Navegación ---
    fun selectYear(year: DiaryYear) {
        _uiState.update {
            it.copy(
                currentYear = year, //Guarda el año seleccionado
                isShowingYearList = false, //Oculta la lista de años
                isShowingMonthList = true //Muestra la lista de meses
            )
        }
        updateScreenData() //Actualiza los datos de la pantalla

    }

    fun selectMonth(month: DiaryMonth) { //Guarda el mes seleccionado
        _uiState.update { it.copy(currentMonth = month, isShowingMonthList = false) } //Oculta la lista de meses
        updateScreenData() //Actualiza los datos de la pantalla, dando las entradas de ese mes

    }

    fun navigateBack() {
        _uiState.update { state ->
            if (state.currentMonth != null) { //Si se está en un mes, vuelve a la lista de meses
                state.copy(currentMonth = null, isShowingMonthList = true)
            } else if (state.currentYear != null) { //Si se está en un año, vuelve a la lista de años
                state.copy(currentYear = null, isShowingYearList = true, isShowingMonthList = false)
            } else { //Si no se está en ningún nivel, no hace nada
                state
            }
        }
        updateScreenData() //Actualiza los datos de la pantalla

    }
}