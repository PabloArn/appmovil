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

    // --- Lógica de CRUD (Agregar) ---
    fun addYear(title: String, imageUri: Uri?) {
        //Generamos un id para el año, si la lista de años está vacía, el id es 1, si no, el id es el máximo id + 1
        val newId = if (allYears.isEmpty()) 1 else allYears.maxOf { it.id } + 1
        allYears.add(DiaryYear(newId, title, imageUri)) //Añadimos el año a la lista de años
        updateScreenData()
    }

    private fun getMonthNumber(monthName: String): Int {
        //Limpiamos el nombre del mes para que no haya problemas con mayúsculas y minúsculas
        val cleanName = monthName.trim().lowercase()
        //Comprobamos el nombre del mes y devolvemos el número correspondiente
        return when {
            cleanName.startsWith("ene") -> 1
            cleanName.startsWith("feb") -> 2
            cleanName.startsWith("mar") -> 3
            cleanName.startsWith("abr") -> 4
            cleanName.startsWith("may") -> 5
            cleanName.startsWith("jun") -> 6
            cleanName.startsWith("jul") -> 7
            cleanName.startsWith("ago") -> 8
            cleanName.startsWith("sep") -> 9
            cleanName.startsWith("oct") -> 10
            cleanName.startsWith("nov") -> 11
            cleanName.startsWith("dic") -> 12
            else -> 13 //Si no coincide con ningún mes, devolvemos al final
        }
    }

    fun addMonth(yearId: Int, title: String, imageUri: Uri?) {
        val realOrder = getMonthNumber(title) //Obtenemos el número del mes
        //Generamos un id para el mes, si la lista de meses está vacía, el id es 1, si no, el id es el máximo id + 1
        val newId = if (allMonths.isEmpty()) 1 else allMonths.maxOf { it.id } + 1
        //Creamos el mes y lo añadimos a la lista de meses
        val newMonth = DiaryMonth(id = newId, yearId = yearId, title = title, chronologicalOrder = realOrder, coverImageUri = imageUri)
        allMonths.add(newMonth)
        updateScreenData()
    }

    fun addEntryNote(monthId: Int, text: String, imageUri: Uri?) {
        val newId = if (allEntries.isEmpty()) 1 else allEntries.maxOf { it.id } + 1 //Generamos un id para la nota
        //Creamos la nota con su id, mes, texto e imagen
        val newEntry = DiaryEntry(id = newId, monthId = monthId, titleText = text, entryImageUri = imageUri)
        allEntries.add(newEntry)
        updateScreenData()
    }

    // --- Lógica de CRUD (Borrar) ---
    fun deleteYear(year: DiaryYear) {
        allYears.remove(year)
        // Borrado en cascada: Borramos los meses de ese año y las notas de esos meses
        val monthsToRemove = allMonths.filter { it.yearId == year.id }
        allMonths.removeAll(monthsToRemove)
        val monthIds = monthsToRemove.map { it.id }
        allEntries.removeAll { it.monthId in monthIds }
        updateScreenData()
    }

    fun deleteMonth(month: DiaryMonth) {
        allMonths.remove(month)
        // Borrado en cascada: Borramos las notas de ese mes
        allEntries.removeAll { it.monthId == month.id }
        updateScreenData()
    }

    fun deleteEntry(entry: DiaryEntry) {
        //Aquí solo borramos la entrada o nota elegida
        allEntries.remove(entry)
        updateScreenData()
    }
}