package com.example.myday

import android.net.Uri

data class DiaryYear(
    val id: Int,
    val title: String, //Título del año, aquí agregamos el nombre de nuestra primera "carpeta" que sería el año
    val coverImageUri: Uri? = null //Usamos Uri reales que llamamos gracias a las librerías de Coil
)

data class DiaryMonth(
    val id: Int,
    val yearId: Int,
    val title: String, //Título del mes, aquí agregamos el nombre de nuestra segunda "carpeta" que sería el mes
    val chronologicalOrder: Int, //Agrega el orden a la lista de los meses, enero -> 1, febrero -> 2, etc.
    val coverImageUri: Uri? = null
)

data class DiaryEntry(
    val id: Int,
    val monthId: Int,
    val titleText: String, //Título del día, aquí agregamos el nombre de nuestra tercera "carpeta" que sería el día
    val entryImageUri: Uri? = null
)