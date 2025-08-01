package com.skyd.podaura.ui.screen.settings.playerconfig

import android.os.Build
import android.os.Parcelable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.skyd.compone.component.BackIcon
import com.skyd.compone.component.CheckableListMenu
import com.skyd.compone.component.ComponeIconButton
import com.skyd.compone.component.ComponeTopBar
import com.skyd.compone.component.ComponeTopBarStyle
import com.skyd.compone.component.DefaultBackClick
import com.skyd.compone.component.dialog.SliderDialog
import com.skyd.compone.local.LocalNavController
import com.skyd.podaura.ext.fileSize
import com.skyd.podaura.ext.toSignedString
import com.skyd.podaura.model.preference.player.BackgroundPlayPreference
import com.skyd.podaura.model.preference.player.PlayerAutoPipPreference
import com.skyd.podaura.model.preference.player.PlayerDoubleTapPreference
import com.skyd.podaura.model.preference.player.PlayerForwardSecondsButtonValuePreference
import com.skyd.podaura.model.preference.player.PlayerMaxBackCacheSizePreference
import com.skyd.podaura.model.preference.player.PlayerMaxCacheSizePreference
import com.skyd.podaura.model.preference.player.PlayerSeekOptionPreference
import com.skyd.podaura.model.preference.player.PlayerShowForwardSecondsButtonPreference
import com.skyd.podaura.model.preference.player.PlayerShowProgressIndicatorPreference
import com.skyd.podaura.model.preference.player.PlayerShowScreenshotButtonPreference
import com.skyd.podaura.ui.screen.settings.playerconfig.advanced.PlayerConfigAdvancedRoute
import com.skyd.settings.BaseSettingsItem
import com.skyd.settings.SettingsLazyColumn
import com.skyd.settings.SwitchBaseSettingsItem
import com.skyd.settings.SwitchSettingsItem
import com.skyd.settings.suspendString
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import podaura.shared.generated.resources.Res
import podaura.shared.generated.resources.cancel
import podaura.shared.generated.resources.ok
import podaura.shared.generated.resources.player_config_advanced_screen_name
import podaura.shared.generated.resources.player_config_screen_advanced_category
import podaura.shared.generated.resources.player_config_screen_appearance_category
import podaura.shared.generated.resources.player_config_screen_auto_pip
import podaura.shared.generated.resources.player_config_screen_auto_pip_description
import podaura.shared.generated.resources.player_config_screen_background_play
import podaura.shared.generated.resources.player_config_screen_background_play_description
import podaura.shared.generated.resources.player_config_screen_behavior_category
import podaura.shared.generated.resources.player_config_screen_cache_category
import podaura.shared.generated.resources.player_config_screen_double_tap
import podaura.shared.generated.resources.player_config_screen_forward_second_button_value
import podaura.shared.generated.resources.player_config_screen_max_back_cache_size
import podaura.shared.generated.resources.player_config_screen_max_cache_size
import podaura.shared.generated.resources.player_config_screen_name
import podaura.shared.generated.resources.player_config_screen_seek_option
import podaura.shared.generated.resources.player_config_screen_show_forward_seconds_button
import podaura.shared.generated.resources.player_config_screen_show_forward_seconds_button_description
import podaura.shared.generated.resources.player_config_screen_show_progress_indicator
import podaura.shared.generated.resources.player_config_screen_show_progress_indicator_description
import podaura.shared.generated.resources.player_config_screen_show_screenshot_button
import podaura.shared.generated.resources.player_config_screen_show_screenshot_button_description
import podaura.shared.generated.resources.reset


@Serializable
@Parcelize
data object PlayerConfigRoute : Parcelable

@Composable
fun PlayerConfigScreen(onBack: (() -> Unit)? = DefaultBackClick) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    var expandDoubleTapMenu by rememberSaveable { mutableStateOf(false) }
    var expandSeekOptionMenu by rememberSaveable { mutableStateOf(false) }
    var openForwardSecondButtonValueDialog by rememberSaveable { mutableStateOf(false) }
    var openMaxCacheSizeDialog by rememberSaveable { mutableStateOf(false) }
    var openMaxBackCacheSizeDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ComponeTopBar(
                style = ComponeTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(Res.string.player_config_screen_name)) },
                navigationIcon = { if (onBack != null) BackIcon(onClick = onBack) },
            )
        }
    ) { paddingValues ->
        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            group(text = { getString(Res.string.player_config_screen_behavior_category) }) {
                item {
                    SwitchSettingsItem(
                        imageVector = Icons.Outlined.Speaker,
                        text = stringResource(Res.string.player_config_screen_background_play),
                        description = stringResource(Res.string.player_config_screen_background_play_description),
                        checked = BackgroundPlayPreference.current,
                        onCheckedChange = { BackgroundPlayPreference.put(scope, it) }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.TouchApp),
                        text = stringResource(Res.string.player_config_screen_double_tap),
                        descriptionText = suspendString(PlayerDoubleTapPreference.current) {
                            PlayerDoubleTapPreference.toDisplayName(it)
                        },
                        extraContent = {
                            DoubleTapMenu(
                                expanded = expandDoubleTapMenu,
                                onDismissRequest = { expandDoubleTapMenu = false }
                            )
                        },
                        onClick = { expandDoubleTapMenu = true },
                    )
                }
                item {
                    SwitchSettingsItem(
                        imageVector = Icons.Outlined.PictureInPictureAlt,
                        text = stringResource(Res.string.player_config_screen_auto_pip),
                        description = stringResource(Res.string.player_config_screen_auto_pip_description),
                        checked = PlayerAutoPipPreference.current,
                        onCheckedChange = { PlayerAutoPipPreference.put(scope, it) }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Redo),
                        text = stringResource(Res.string.player_config_screen_seek_option),
                        descriptionText = suspendString(PlayerSeekOptionPreference.current) {
                            PlayerSeekOptionPreference.toDisplayName(it)
                        },
                        extraContent = {
                            SeekOptionMenu(
                                expanded = expandSeekOptionMenu,
                                onDismissRequest = { expandSeekOptionMenu = false },
                            )
                        },
                        onClick = { expandSeekOptionMenu = true },
                    )
                }
            }
            group(text = { getString(Res.string.player_config_screen_appearance_category) }) {
                item {
                    SwitchSettingsItem(
                        imageVector = Icons.Outlined.PhotoCamera,
                        text = stringResource(Res.string.player_config_screen_show_screenshot_button),
                        description = stringResource(Res.string.player_config_screen_show_screenshot_button_description),
                        checked = PlayerShowScreenshotButtonPreference.current,
                        onCheckedChange = { PlayerShowScreenshotButtonPreference.put(scope, it) }
                    )
                }
                item {
                    SwitchSettingsItem(
                        imageVector = Icons.Outlined.Timelapse,
                        text = stringResource(Res.string.player_config_screen_show_progress_indicator),
                        description = stringResource(Res.string.player_config_screen_show_progress_indicator_description),
                        checked = PlayerShowProgressIndicatorPreference.current,
                        onCheckedChange = { PlayerShowProgressIndicatorPreference.put(scope, it) }
                    )
                }
                item {
                    val forwardSeconds = PlayerForwardSecondsButtonValuePreference.current
                    SwitchBaseSettingsItem(
                        imageVector = if (forwardSeconds >= 0) Icons.Outlined.FastForward else Icons.Outlined.FastRewind,
                        text = stringResource(
                            Res.string.player_config_screen_show_forward_seconds_button,
                            forwardSeconds.toSignedString()
                        ),
                        description = stringResource(
                            Res.string.player_config_screen_show_forward_seconds_button_description,
                            forwardSeconds.toSignedString()
                        ),
                        checked = PlayerShowForwardSecondsButtonPreference.current,
                        onCheckedChange = {
                            PlayerShowForwardSecondsButtonPreference.put(
                                scope,
                                it
                            )
                        },
                        onClick = { openForwardSecondButtonValueDialog = true },
                    )
                }
            }
            group(text = { getString(Res.string.player_config_screen_cache_category) }) {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.KeyboardArrowRight),
                        text = stringResource(Res.string.player_config_screen_max_cache_size),
                        descriptionText = PlayerMaxCacheSizePreference.current.fileSize(context),
                        onClick = { openMaxCacheSizeDialog = true }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.KeyboardArrowLeft),
                        text = stringResource(Res.string.player_config_screen_max_back_cache_size),
                        descriptionText = PlayerMaxBackCacheSizePreference.current.fileSize(context),
                        onClick = { openMaxBackCacheSizeDialog = true }
                    )
                }
            }
            group(text = { getString(Res.string.player_config_screen_advanced_category) }) {
                item {
                    BaseSettingsItem(
                        icon = null,
                        text = stringResource(Res.string.player_config_advanced_screen_name),
                        descriptionText = null,
                        onClick = { navController.navigate(PlayerConfigAdvancedRoute) },
                    )
                }
            }
        }

        if (openForwardSecondButtonValueDialog) {
            ForwardSecondButtonValueDialog(onDismissRequest = {
                openForwardSecondButtonValueDialog = false
            })
        }

        if (openMaxCacheSizeDialog) {
            MaxCacheSizeDialog(
                onDismissRequest = { openMaxCacheSizeDialog = false },
                title = stringResource(Res.string.player_config_screen_max_cache_size),
                initValue = PlayerMaxCacheSizePreference.current,
                defaultValue = { PlayerMaxCacheSizePreference.default },
                onConfirm = { PlayerMaxCacheSizePreference.put(scope, it) }
            )
        }
        if (openMaxBackCacheSizeDialog) {
            MaxCacheSizeDialog(
                onDismissRequest = { openMaxBackCacheSizeDialog = false },
                title = stringResource(Res.string.player_config_screen_max_back_cache_size),
                initValue = PlayerMaxBackCacheSizePreference.current,
                defaultValue = { PlayerMaxBackCacheSizePreference.default },
                onConfirm = { PlayerMaxBackCacheSizePreference.put(scope, it) }
            )
        }
    }
}

@Composable
private fun DoubleTapMenu(expanded: Boolean, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()

    CheckableListMenu(
        expanded = expanded,
        current = PlayerDoubleTapPreference.current,
        values = remember { PlayerDoubleTapPreference.values.toList() },
        displayName = { PlayerDoubleTapPreference.toDisplayName(it) },
        onChecked = { PlayerDoubleTapPreference.put(scope, it) },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun SeekOptionMenu(expanded: Boolean, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    CheckableListMenu(
        expanded = expanded,
        current = PlayerSeekOptionPreference.current,
        values = remember { PlayerSeekOptionPreference.values.toList() },
        displayName = { PlayerSeekOptionPreference.toDisplayName(it) },
        onChecked = { PlayerSeekOptionPreference.put(scope, it) },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
internal fun ForwardSecondButtonValueDialog(onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val forwardSeconds = PlayerForwardSecondsButtonValuePreference.current
    var value by rememberSaveable { mutableIntStateOf(forwardSeconds) }

    SliderDialog(
        onDismissRequest = onDismissRequest,
        value = value.toFloat(),
        onValueChange = { value = it.toInt() },
        valueRange = PlayerForwardSecondsButtonValuePreference.range,
        valueLabel = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .animateContentSize(),
                    text = "${value.toInt().toSignedString()}s",
                    style = MaterialTheme.typography.titleMedium,
                )
                ComponeIconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { value = PlayerForwardSecondsButtonValuePreference.default },
                    imageVector = Icons.Outlined.Restore,
                    contentDescription = stringResource(Res.string.reset),
                )
            }
        },
        icon = {
            Icon(
                imageVector = if (value >= 0) Icons.Outlined.FastForward else Icons.Outlined.FastRewind,
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(Res.string.player_config_screen_forward_second_button_value)) },
        confirmButton = {
            TextButton(onClick = {
                PlayerForwardSecondsButtonValuePreference.put(scope, value)
                onDismissRequest()
            }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
internal fun MaxCacheSizeDialog(
    onDismissRequest: () -> Unit,
    title: String,
    initValue: Long,
    defaultValue: () -> Long,
    onConfirm: (Long) -> Unit,
) {
    var value by rememberSaveable { mutableLongStateOf(initValue) }
    val context = LocalContext.current

    // 64 MB / 32 MB
    val maxSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) 64f else 32f

    SliderDialog(
        onDismissRequest = onDismissRequest,
        value = value / 1048576f,
        onValueChange = { value = it.toLong() * 1048576 },
        valueRange = 1f..maxSize,
        valueLabel = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .animateContentSize(),
                    text = value.fileSize(context),
                    style = MaterialTheme.typography.titleMedium,
                )
                ComponeIconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { value = defaultValue() },
                    imageVector = Icons.Outlined.Restore,
                    contentDescription = stringResource(Res.string.reset),
                )
            }
        },
        icon = { Icon(imageVector = Icons.Outlined.Save, contentDescription = null) },
        title = { Text(text = title) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(value)
                onDismissRequest()
            }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}