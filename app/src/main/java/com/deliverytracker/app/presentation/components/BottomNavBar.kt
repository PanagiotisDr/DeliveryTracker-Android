package com.deliverytracker.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deliverytracker.app.R
import com.deliverytracker.app.presentation.theme.*

/**
 * 🧭 BottomNavBar - Premium animated bottom navigation
 * 
 * Material 3 bottom navigation with:
 * - Animated selection indicator
 * - Icon scale animation
 * - Haptic feedback
 * - Theme-aware colors (αντί hardcoded Dark colors)
 * 
 * @author DeliveryTracker Team
 * @version 1.1.0 - Theme-aware update
 * @since 2026-02
 */

/**
 * Navigation item data class
 */
data class NavItem(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Default navigation items for the app
 */
val defaultNavItems = listOf(
    NavItem(
        route = "dashboard",
        labelResId = R.string.nav_dashboard,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    NavItem(
        route = "shifts",
        labelResId = R.string.nav_shifts,
        selectedIcon = Icons.Filled.WorkHistory,
        unselectedIcon = Icons.Outlined.WorkHistory
    ),
    NavItem(
        route = "expenses",
        labelResId = R.string.nav_expenses,
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    ),
    NavItem(
        route = "statistics",
        labelResId = R.string.nav_statistics,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    ),
    NavItem(
        route = "settings",
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

/**
 * Premium animated bottom navigation bar
 * Χρησιμοποιεί MaterialTheme.colorScheme για proper theme support
 * 
 * @param currentRoute Current selected route
 * @param onNavigate Callback when navigation item is clicked
 * @param navItems List of navigation items
 * @param modifier Modifier for the nav bar
 */
@Composable
fun PremiumBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    navItems: List<NavItem> = defaultNavItems,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.surfaceContainer.copy(alpha = 0.95f),
        shadowElevation = Dimensions.elevationLg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Respect system navigation bar
                .height(Dimensions.bottomNavHeight)
                .padding(horizontal = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                
                NavBarItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(item.route)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual navigation bar item with animations
 * Χρησιμοποιεί MaterialTheme.colorScheme για proper theme support
 */
@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = AnimationSpecs.springBouncy(),
        label = "scale"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = AnimationSpecs.default(),
        label = "iconColor"
    )
    
    val labelAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = AnimationSpecs.default(),
        label = "labelAlpha"
    )
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.radiusMd))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with selection indicator
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Selection indicator pill
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(Spacing.huge)
                        .height(Spacing.xxl)
                        .clip(RoundedCornerShape(Spacing.radiusFull))
                        .background(colorScheme.primary.copy(alpha = 0.15f))
                )
            }
            
            // Icon
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = stringResource(item.labelResId),
                modifier = Modifier
                    .scale(scale)
                    .size(Dimensions.iconMd),
                tint = iconColor
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.xxs))
        
        // Label
        Text(
            text = stringResource(item.labelResId),
            style = CustomTextStyles.NavigationLabel.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = labelAlpha)
        )
    }
}

/**
 * Alternative: Glass morphism bottom nav bar
 * Χρησιμοποιεί MaterialTheme.colorScheme για proper theme support
 */
@Composable
fun GlassBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    navItems: List<NavItem> = defaultNavItems,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Respect system navigation bar
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        GlassCard(
            backgroundColor = colorScheme.surfaceContainer.copy(alpha = 0.8f),
            cornerRadius = Spacing.radiusXxl,
            borderColor = colorScheme.outlineVariant.copy(alpha = 0.2f),
            contentPadding = PaddingValues(vertical = Spacing.sm, horizontal = Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    
                    GlassNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigate(item.route)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Glass nav item with pill indicator
 * Χρησιμοποιεί MaterialTheme.colorScheme για proper theme support
 */
@Composable
private fun GlassNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = AnimationSpecs.default(),
        label = "iconColor"
    )
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.radiusMd))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                .clip(RoundedCornerShape(Spacing.radiusFull))
                .background(
                    if (isSelected) colorScheme.primary.copy(alpha = 0.15f)
                    else Color.Transparent
                )
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = stringResource(item.labelResId),
                modifier = Modifier.size(Dimensions.iconMd),
                tint = iconColor
            )
        }
        
        Text(
            text = stringResource(item.labelResId),
            style = MaterialTheme.typography.labelSmall,
            color = iconColor
        )
    }
}

/**
 * Floating action button for quick add
 * Χρησιμοποιεί MaterialTheme.colorScheme για proper theme support
 */
@Composable
fun QuickAddFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = false,
    label: String = ""
) {
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    
    if (extended && label.isNotEmpty()) {
        ExtendedFloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier,
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            },
            text = { Text(text = label) }
        )
    } else {
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            modifier = modifier,
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }
}

/**
 * Alias for backward compatibility
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumBottomNavBar(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        modifier = modifier
    )
}
