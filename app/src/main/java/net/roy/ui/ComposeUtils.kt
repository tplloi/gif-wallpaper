package net.roy.ui

import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import net.redwarp.gifwallpaper.R

@Composable
fun BasicTopBar(modifier: Modifier = Modifier, title: String, navController: NavController) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Text(text = title)
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back))
                }
            },
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
        )
    }
}

@Composable
fun UpdateStatusBarColors(
    color: Color = Color.Transparent,
    darkIcons: Boolean = color.luminance() > 0.5f,
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = darkIcons,
        )
    }
}
