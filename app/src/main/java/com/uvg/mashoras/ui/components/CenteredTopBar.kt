package com.uvg.mashoras.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uvg.mashoras.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopBar(
    modifier: Modifier = Modifier,
    logoSize: Dp = 200.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(logoSize), contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier
                            .size(logoSize)
                            .padding(end = 19.dp, bottom = 10.dp),
                        painter = painterResource(id = R.drawable.logomashoras),
                        contentDescription = "Logo MasHoras",
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorScheme.primary
        )
    )
}