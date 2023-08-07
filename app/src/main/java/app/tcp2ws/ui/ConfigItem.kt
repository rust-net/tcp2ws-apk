package app.tcp2ws.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.tcp2ws.Config
import app.tcp2ws.Instance
import app.tcp2ws.isRunning
import app.tcp2ws.playOrStop
import app.tcp2ws.rm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDialog(
    showMenu: MutableState<Boolean>,
    showDialog: MutableState<Boolean>,
    config: Config,
) {
    AlertDialog(
        onDismissRequest = { showMenu.value = false },
    ) {
        Column {
            Button(onClick = {
                if (isRunning(config.name)) return@Button showToast("运行中，不允许修改！")
                showMenu.value = false
                showDialog.value = true
            }, Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.background)
                Spacer(modifier = Modifier.width(12.dp))
                Text("修改")
            }
            Button(onClick = {
                if (isRunning(config.name)) return@Button showToast("运行中，不允许删除！")
                val ok = when (config) {
                    is Instance -> rm<Instance>(config.name)
                }
                if (ok)
                    showToast("删除 ${config.name} 成功")
                showMenu.value = false
            }, Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.background)
                Spacer(modifier = Modifier.width(12.dp))
                Text("删除")
            }
        }
    }
}

@Composable
fun InstanceItem(
    value: Instance
) {
    val showMenu= remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val running = remember { mutableStateOf(isRunning(value.name)) }
    if (showMenu.value) {
        MenuDialog(showMenu, showDialog, value)
    }
    if (showDialog.value) {
        ConfigDialog(
            showDialog,
            instance = value,
            isModify = true,
        )
    }
    Spacer(modifier = Modifier.size(10.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    showMenu.value = true
                })
            },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.Center,
        ) {
            Text(text = value.name)
        }
        Divider()
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceAround,
                ) {
                    Column {
                        Text(
                            text = "Websocket:",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = value.ws,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 200.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Listen:",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = value.listen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceAround,
                ) {
                    IconButton(onClick = { playOrStop(value, running) }) {
                        Icon(if (!running.value) Icons.Filled.PlayArrow else Icons.Filled.Close, null, tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}