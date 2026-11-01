package com.example.sleeptheory01

data class SoundItem(val name: String, val resId: Int)

object SoundLibrary {
    val sounds: List<SoundItem> = listOf(
        SoundItem("Sonido de lluvia", R.raw.rain),
        SoundItem("Sonido de océano", R.raw.ocean),
        SoundItem("Sonido blanco", R.raw.white),
        SoundItem("Sonido de naturaleza", R.raw.bird),
        SoundItem("Sonido de naturaleza 2", R.raw.natural),
        SoundItem("Sonido de DS2", R.raw.ds2),
        SoundItem("Sonido de H3 ODST", R.raw.halo),
        SoundItem("Sonido de RE7", R.raw.re7)
    )
}