package com.palrasp.myapplication.CalendarClasses

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

val fakeClasses = listOf(
    Event(
        name = "Technika mikroprocesorowa",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-18T09:45:00"),
        end = LocalDateTime.parse("2021-05-18T12:00:00"),
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    Event(
        name = "Systemy wspomagania decyzji",
        color = Color(0xFF6DD3CE),
        start = LocalDateTime.parse("2021-05-18T13:15:00"),
        end = LocalDateTime.parse("2021-05-18T14:45:00"),
        description = "Learn about the latest design improvements to help you build personal dynamic experiences with Material Design.",
    ),
    Event(
        name = "Badania operacyjne 2",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-19T09:45:00"),
        end = LocalDateTime.parse("2021-05-19T11:15:00"),
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
    Event(
        name = "Teoria sterowania",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-19T18:30:00"),
        end = LocalDateTime.parse("2021-05-19T20:00:00"),
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    Event(
        name = "Metody optymalizacji",
        color = Color(0xFF6DD3CE),
        start = LocalDateTime.parse("2021-05-20T15:00:00"),
        end = LocalDateTime.parse("2021-05-20T16:30:00"),
        description = "Learn about the latest design improvements to help you build personal dynamic experiences with Material Design.",
    ),
    Event(
        name = "Aparatura automatyzacji",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-21T13:15:00"),
        end = LocalDateTime.parse("2021-05-21T14:45:00"),
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
    Event(
        name = "Analiza i bazy danych",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-22T09:45:00"),
        end = LocalDateTime.parse("2021-05-22T11:15:00"),
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
    Event(
        name = "Metody optymalizacji",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-22T13:15:00"),
        end = LocalDateTime.parse("2021-05-22T14:45:00"),
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
)