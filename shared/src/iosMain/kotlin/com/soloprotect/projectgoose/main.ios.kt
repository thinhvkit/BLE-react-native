import androidx.compose.ui.window.ComposeUIViewController
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.presentation.App

fun mainViewController() = ComposeUIViewController {
    App(Context())
}

