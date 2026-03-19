package com.example.myday


import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryApp(viewModel: MyDayViewModel = viewModel()) {
    //Obtenemos el estado de la aplicación a través del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    //Estas variables controlan si se muestra cada una de las pantallas
    var showAddYearDialog by remember { mutableStateOf(false) }
    var showAddMonthDialog by remember { mutableStateOf(false) }
    var showAddEntryDialog by remember { mutableStateOf(false) }

    Scaffold(
        // Usamos el color principal definido en el tema
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        //El titulo cambia dependiendo de la pantalla
                        text = when {
                            uiState.isShowingYearList -> "My Day :)"
                            uiState.isShowingMonthList -> "${uiState.currentYear?.title ?: ""}"
                            else -> "${uiState.currentMonth?.title ?: ""}"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    //Solo mostramos la flecha de retroceso si no estamos en la pantalla de años
                    if (!uiState.isShowingYearList) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                //Se definen los colores de la barra superior
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Cambia su función de acción según la pantalla
            FloatingActionButton(
                onClick = {
                    when {
                        uiState.isShowingYearList -> showAddYearDialog = true
                        uiState.isShowingMonthList -> showAddMonthDialog = true
                        else -> showAddEntryDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        BackHandler(enabled = !uiState.isShowingYearList) {
            viewModel.navigateBack()
        }

        when {
            uiState.isShowingYearList -> {
                YearListScreen(
                    years = uiState.years,
                    onYearClick = { viewModel.selectYear(it) },
                    onDeleteClick = { viewModel.deleteYear(it) },
                    modifier = modifier
                )
            }
            uiState.isShowingMonthList -> {
                MonthListScreen(
                    months = uiState.months,
                    onMonthClick = { viewModel.selectMonth(it) },
                    onDeleteClick = { viewModel.deleteMonth(it) },
                    modifier = modifier
                )
            }
            else -> {
                EntryListScreen(
                    currentMonth = uiState.currentMonth,
                    entriesForMonth = uiState.entries,
                    onDeleteClick = { viewModel.deleteEntry(it) },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
//Aquí abre la galería para seleccionar una imagen
fun PhotoPickerField(selectedUri: Uri?, onUriSelected: (Uri?) -> Unit, modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> onUriSelected(uri) }
    Box(
        modifier = modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        contentAlignment = Alignment.Center
    ) {
        if (selectedUri != null) {
            //Si hay una imagen seleccionada, la mostramos
            AsyncImage(model = selectedUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            IconButton(onClick = { onUriSelected(null) }, modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(0.5f), RoundedCornerShape(0.dp, 12.dp, 0.dp, 12.dp))) {
                Icon(Icons.Default.Add, contentDescription = "Quitar foto", tint = Color.White)
            }
        } else {
            //Si no hay imagen seleccionada, mostramos un icono con un texto
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Toca para agregar foto", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

//Pantallas con colores de los temas
@Composable
fun YearListScreen(years: List<DiaryYear>, onYearClick: (DiaryYear) -> Unit, onDeleteClick: (DiaryYear) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(years) { year ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onYearClick(year) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    if (year.coverImageUri != null) {
                        AsyncImage(model = year.coverImageUri, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text(text = "Sin foto de portada", color = Color.Gray)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = year.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        //Botón que borra el año
                        IconButton(onClick = { onDeleteClick(year) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar Año", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthListScreen(months: List<DiaryMonth>, onMonthClick: (DiaryMonth) -> Unit, onDeleteClick: (DiaryMonth) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(months) { month ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onMonthClick(month) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (month.coverImageUri != null) {
                        AsyncImage(model = month.coverImageUri, contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = month.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { onDeleteClick(month) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar Mes", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun EntryListScreen(currentMonth: DiaryMonth?, entriesForMonth: List<DiaryEntry>, onDeleteClick: (DiaryEntry) -> Unit, modifier: Modifier = Modifier) {
    //Esta pantalla muestra las entradas de un mes seleccionado
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text(text = currentMonth?.title ?: "", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onBackground)
        }

        if (entriesForMonth.isNotEmpty()) {
            item {
                //Si hay entradas con imagen, se muestran en fila horizontal
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(entriesForMonth) { entry ->
                        if (entry.entryImageUri != null) {
                            AsyncImage(model = entry.entryImageUri, contentDescription = null, modifier = Modifier.width(160.dp).height(100.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                        } else {
                            //Si no hay imagen, se muestra un recuadro gris
                            Box(modifier = Modifier.width(160.dp).height(100.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item { Text(text = "• Notas del mes", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground) }

        items(entriesForMonth) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (entry.entryImageUri != null) {
                        AsyncImage(model = entry.entryImageUri, contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = entry.titleText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = { onDeleteClick(entry) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar Nota", tint = Color.Red)
                    }
                }
            }
        }
    }
}
