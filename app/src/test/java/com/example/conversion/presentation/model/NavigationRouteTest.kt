package com.example.conversion.presentation.model

import com.example.conversion.navigation.Route
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for NavigationRoute model and NavigationRoutes object.
 * Tests route mapping, categorization, and lookup functionality.
 */
class NavigationRouteTest {

    @Test
    fun `NavigationRoute has required properties`() {
        val route = NavigationRoutes.HOME
        
        assertNotNull(route.route)
        assertNotNull(route.id)
        assertNotNull(route.label)
        assertNotNull(route.icon)
        assertNotNull(route.iconOutlined)
        assertEquals(NavigationCategory.PRIMARY, route.category)
        assertTrue(route.isVisible)
    }

    @Test
    fun `ALL_ROUTES contains all navigation items`() {
        val allRoutes = NavigationRoutes.ALL_ROUTES
        
        assertTrue(allRoutes.isNotEmpty())
        assertTrue(allRoutes.contains(NavigationRoutes.HOME))
        assertTrue(allRoutes.contains(NavigationRoutes.FILE_SELECTION))
        assertTrue(allRoutes.contains(NavigationRoutes.SETTINGS))
    }

    @Test
    fun `PRIMARY_ROUTES contains only primary category items`() {
        val primaryRoutes = NavigationRoutes.PRIMARY_ROUTES
        
        assertTrue(primaryRoutes.isNotEmpty())
        assertTrue(primaryRoutes.all { it.category == NavigationCategory.PRIMARY })
        assertTrue(primaryRoutes.contains(NavigationRoutes.HOME))
        assertTrue(primaryRoutes.contains(NavigationRoutes.FILE_SELECTION))
    }

    @Test
    fun `TOOL_ROUTES contains only tools category items`() {
        val toolRoutes = NavigationRoutes.TOOL_ROUTES
        
        assertTrue(toolRoutes.isNotEmpty())
        assertTrue(toolRoutes.all { it.category == NavigationCategory.TOOLS })
        assertTrue(toolRoutes.contains(NavigationRoutes.AI_SUGGESTIONS))
        assertTrue(toolRoutes.contains(NavigationRoutes.REGEX_BUILDER))
    }

    @Test
    fun `MANAGEMENT_ROUTES contains only management category items`() {
        val managementRoutes = NavigationRoutes.MANAGEMENT_ROUTES
        
        assertTrue(managementRoutes.isNotEmpty())
        assertTrue(managementRoutes.all { it.category == NavigationCategory.MANAGEMENT })
        assertTrue(managementRoutes.contains(NavigationRoutes.SETTINGS))
        assertTrue(managementRoutes.contains(NavigationRoutes.TAG_MANAGEMENT))
    }

    @Test
    fun `INTEGRATION_ROUTES contains only integration category items`() {
        val integrationRoutes = NavigationRoutes.INTEGRATION_ROUTES
        
        assertTrue(integrationRoutes.isNotEmpty())
        assertTrue(integrationRoutes.all { it.category == NavigationCategory.INTEGRATION })
        assertTrue(integrationRoutes.contains(NavigationRoutes.CLOUD_SYNC))
        assertTrue(integrationRoutes.contains(NavigationRoutes.ACCOUNT))
    }

    @Test
    fun `HISTORY_ROUTES contains only history category items`() {
        val historyRoutes = NavigationRoutes.HISTORY_ROUTES
        
        assertTrue(historyRoutes.isNotEmpty())
        assertTrue(historyRoutes.all { it.category == NavigationCategory.HISTORY })
        assertTrue(historyRoutes.contains(NavigationRoutes.HISTORY))
    }

    @Test
    fun `findById returns correct navigation route`() {
        val homeRoute = NavigationRoutes.findById("home")
        assertNotNull(homeRoute)
        assertEquals(NavigationRoutes.HOME, homeRoute)

        val settingsRoute = NavigationRoutes.findById("settings")
        assertNotNull(settingsRoute)
        assertEquals(NavigationRoutes.SETTINGS, settingsRoute)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        val route = NavigationRoutes.findById("non_existent_id")
        assertNull(route)
    }

    @Test
    fun `findByRoute returns correct navigation route for simple routes`() {
        val homeRoute = NavigationRoutes.findByRoute(Route.Home)
        assertNotNull(homeRoute)
        assertEquals(NavigationRoutes.HOME, homeRoute)

        val settingsRoute = NavigationRoutes.findByRoute(Route.Settings)
        assertNotNull(settingsRoute)
        assertEquals(NavigationRoutes.SETTINGS, settingsRoute)
    }

    @Test
    fun `all navigation items have unique ids`() {
        val allIds = NavigationRoutes.ALL_ROUTES.map { it.id }
        val uniqueIds = allIds.distinct()
        
        assertEquals(allIds.size, uniqueIds.size, "All navigation items should have unique IDs")
    }

    @Test
    fun `all navigation items have non-empty labels`() {
        NavigationRoutes.ALL_ROUTES.forEach { route ->
            assertTrue("Route ${route.id} should have non-empty label", route.label.isNotEmpty())
        }
    }

    @Test
    fun `badge support works correctly`() {
        // AI Suggestions should have a badge
        assertNotNull(NavigationRoutes.AI_SUGGESTIONS.badge)
        assertEquals("AI", NavigationRoutes.AI_SUGGESTIONS.badge)

        // Most routes should not have badges
        assertNull(NavigationRoutes.HOME.badge)
        assertNull(NavigationRoutes.FILE_SELECTION.badge)
    }

    @Test
    fun `badgeCount support works correctly`() {
        // Test that badgeCount can be null (default)
        assertNull(NavigationRoutes.HOME.badgeCount)
        
        // BadgeCount functionality is present in the model
        val routeWithBadge = NavigationRoutes.HOME.copy(badgeCount = 5)
        assertEquals(5, routeWithBadge.badgeCount)
    }

    @Test
    fun `isVisible flag works correctly`() {
        // All routes should be visible by default
        NavigationRoutes.ALL_ROUTES.forEach { route ->
            assertTrue("Route ${route.id} should be visible by default", route.isVisible)
        }

        // Test visibility can be changed
        val hiddenRoute = NavigationRoutes.HOME.copy(isVisible = false)
        assertFalse(hiddenRoute.isVisible)
    }

    @Test
    fun `navigation categories are correctly assigned`() {
        // Primary
        assertEquals(NavigationCategory.PRIMARY, NavigationRoutes.HOME.category)
        assertEquals(NavigationCategory.PRIMARY, NavigationRoutes.FILE_SELECTION.category)

        // Tools
        assertEquals(NavigationCategory.TOOLS, NavigationRoutes.AI_SUGGESTIONS.category)
        assertEquals(NavigationCategory.TOOLS, NavigationRoutes.REGEX_BUILDER.category)

        // Management
        assertEquals(NavigationCategory.MANAGEMENT, NavigationRoutes.SETTINGS.category)
        assertEquals(NavigationCategory.MANAGEMENT, NavigationRoutes.MONITORING.category)

        // Integration
        assertEquals(NavigationCategory.INTEGRATION, NavigationRoutes.CLOUD_SYNC.category)
        assertEquals(NavigationCategory.INTEGRATION, NavigationRoutes.ACCOUNT.category)

        // History
        assertEquals(NavigationCategory.HISTORY, NavigationRoutes.HISTORY.category)
    }

    @Test
    fun `all routes have both filled and outlined icons`() {
        NavigationRoutes.ALL_ROUTES.forEach { route ->
            assertNotNull("Route ${route.id} should have filled icon", route.icon)
            assertNotNull("Route ${route.id} should have outlined icon", route.iconOutlined)
        }
    }

    @Test
    fun `category filtering works correctly`() {
        val allRoutesCount = NavigationRoutes.ALL_ROUTES.size
        val categorizedCount = NavigationRoutes.PRIMARY_ROUTES.size +
                NavigationRoutes.TOOL_ROUTES.size +
                NavigationRoutes.MANAGEMENT_ROUTES.size +
                NavigationRoutes.INTEGRATION_ROUTES.size +
                NavigationRoutes.HISTORY_ROUTES.size

        assertEquals("All routes should be categorized", allRoutesCount, categorizedCount)
    }

    @Test
    fun `HOME route has expected properties`() {
        val home = NavigationRoutes.HOME
        
        assertEquals("home", home.id)
        assertEquals("Home", home.label)
        assertEquals(NavigationCategory.PRIMARY, home.category)
        assertTrue(home.isVisible)
        assertNull(home.badge)
        assertNull(home.badgeCount)
    }

    @Test
    fun `SETTINGS route has expected properties`() {
        val settings = NavigationRoutes.SETTINGS
        
        assertEquals("settings", settings.id)
        assertEquals("Settings", settings.label)
        assertEquals(NavigationCategory.MANAGEMENT, settings.category)
        assertTrue(settings.isVisible)
    }

    @Test
    fun `NavigationRoute copy works correctly`() {
        val original = NavigationRoutes.HOME
        val modified = original.copy(
            label = "Modified Home",
            badge = "New",
            badgeCount = 3,
            isVisible = false
        )

        // Original should be unchanged
        assertEquals("Home", original.label)
        assertNull(original.badge)
        assertNull(original.badgeCount)
        assertTrue(original.isVisible)

        // Modified should have new values
        assertEquals("Modified Home", modified.label)
        assertEquals("New", modified.badge)
        assertEquals(3, modified.badgeCount)
        assertFalse(modified.isVisible)

        // Unchanged properties should match
        assertEquals(original.id, modified.id)
        assertEquals(original.route, modified.route)
        assertEquals(original.category, modified.category)
    }
}
