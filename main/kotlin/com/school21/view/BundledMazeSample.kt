package com.school21.view

data class BundledMazeSample(
    val displayName: String,
    val resourcePath: String
) {
    override fun toString(): String = displayName

    companion object {
        val DEFAULT = BundledMazeSample("20x20 sample", "mazes/maze_20x20.txt")

        val ALL = listOf(
            BundledMazeSample("2x2 sample", "mazes/maze_02x02.txt"),
            BundledMazeSample("4x4 sample", "mazes/maze_04x04.txt"),
            BundledMazeSample("10x10 sample", "mazes/maze_10x10.txt"),
            DEFAULT
        )
    }
}
