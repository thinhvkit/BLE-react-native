package com.myprotect.projectx.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.futura_ot_bold
import myprotect_mobile.shared.generated.resources.opensans_regular

object FontFamilies {
    val openSans: FontFamily
        @Composable
        get() = FontFamily(
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Thin,
                style = FontStyle.Normal
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Normal,
                style = FontStyle.Italic
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Light,
                style = FontStyle.Italic
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Thin,
                style = FontStyle.Italic
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Bold,
                style = FontStyle.Italic
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Black,
                style = FontStyle.Italic
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Black,
                style = FontStyle.Normal
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Light,
                style = FontStyle.Normal
            ),
            Font(
                resource = Res.font.opensans_regular,
                weight = FontWeight.Thin,
                style = FontStyle.Normal,
            ),
        )

    val futuraBold: FontFamily
        @Composable
        get() = FontFamily(
            Font(
                resource = Res.font.futura_ot_bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            ),
        )
}

@Composable
fun OpenSansTypography(): Typography {
    return Typography(
        headlineSmall = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            fontFamily = FontFamilies.openSans
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            fontFamily = FontFamilies.openSans
        ),

        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            fontFamily = FontFamilies.openSans
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            fontFamily = FontFamilies.openSans
        ),
        labelMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            fontFamily = FontFamilies.openSans
        ),
    )
}
