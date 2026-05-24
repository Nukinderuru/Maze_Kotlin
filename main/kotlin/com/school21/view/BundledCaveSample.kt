package com.school21.view

data class BundledCaveSample(
    val displayName: String,
    val resourcePath: String
) {
    override fun toString(): String = displayName

    companion object {
        val DEFAULT = BundledCaveSample("20x20 sample", "caves/cave_20x20.txt")

        val ALL = listOf(
            BundledCaveSample("4x4 sample", "caves/cave_04x04.txt"),
            BundledCaveSample("10x10 sample", "caves/cave_10x10.txt"),
            DEFAULT
        )
    }
}
