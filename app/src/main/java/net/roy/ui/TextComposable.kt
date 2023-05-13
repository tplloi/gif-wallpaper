package net.roy.ui

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.roy.MDDocument
import java.io.InputStream

@Composable
fun MarkdownUi(title: String, fileName: String, navController: NavController) {
    val context = LocalContext.current

    UpdateStatusBarColors()

    val text = remember {
        context.loadMarkdownFile(fileName) ?: ""
    }

    MarkdownPage(
        title = title,
        markdownText = text,
        navController = navController,

        )
}

@Composable
fun MarkdownPage(
    modifier: Modifier = Modifier,
    title: String,
    markdownText: String,
    navController: NavController,
) {
    Scaffold(modifier = modifier, topBar = {
        BasicTopBar(
            title = title,
            navController = navController,
            modifier = Modifier.statusBarsPadding(),
        )
    }) { paddingValues ->
        MDDocument(
            markdown = markdownText,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MarkdownUiPreview() {
    val context = LocalContext.current
    val navController = NavController(context)
    AppTheme {
        MarkdownPage(
            title = "About",
            markdownText = """
            # About
            
            So, this is how the page will look like?

            ## Check the sources

            Sources are available on [github](https://github.com/redwarp/gif-wallpaper).

            Head there if you have feature requests or so.
            """.trimIndent(),
            navController = navController,
        )
    }
}

private fun Context.loadMarkdownFile(markdownFileName: String): String? {
    val url: InputStream? = this::class.java.classLoader?.getResourceAsStream(markdownFileName)
    val content: String?
    if (url != null) {
        content = url.reader().readText()
        url.close()
    } else {
        content = null
    }
    return content
}
