package com.example.datossinmvvm.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.datossinmvvm.data.User
import com.example.datossinmvvm.data.UserDao
import com.example.datossinmvvm.data.UserDatabase
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_db"
        ).build()
    }

    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (firstName.isNotBlank() && lastName.isNotBlank()) {
                                val user = User(0, firstName, lastName)
                                AgregarUsuario(user, dao)
                                firstName = ""
                                lastName = ""
                                dataUser.value = getUsers(dao) // 🔹 Refrescamos la lista automáticamente
                            }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
                    }


                    IconButton(onClick = {
                        coroutineScope.launch {
                            val users = getUsers(dao)
                            dataUser.value = users
                        }
                    }) {
                        Icon(Icons.Default.List, contentDescription = "Listar Usuarios")
                    }


                    IconButton(onClick = {
                        coroutineScope.launch {
                            eliminarUltimoUsuario(dao)
                            dataUser.value = getUsers(dao)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Último")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") }
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = dataUser.value, fontSize = 18.sp)
        }
    }
}


@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    val users = dao.getAll()
    return if (users.isEmpty()) {
        "No hay usuarios registrados"
    } else {
        users.joinToString("\n") { "${it.firstName} - ${it.lastName}" }
    }
}


suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error insert: ${e.message}")
    }
}

suspend fun eliminarUltimoUsuario(dao: UserDao) {
    try {
        dao.deleteLastUser()
    } catch (e: Exception) {
        Log.e("User", "Error al eliminar: ${e.message}")
    }
}

