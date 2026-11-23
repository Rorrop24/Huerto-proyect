package cl.duoc.fotomotion.ui.components

// Convierte "2026-11-11T10:30:15" en "2026-11-11 10:30" para mostrarlo.
fun mostrarMomento(momento: String): String = momento.replace('T', ' ').take(16)
