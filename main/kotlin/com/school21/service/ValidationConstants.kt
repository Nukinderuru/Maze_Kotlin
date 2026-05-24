package com.school21.service

object ValidationConstants {
    const val FIRST_LINE_DIMENSIONS = "The first line must contain exactly two integers: rows and columns"
    const val MAZE_FILE_EMPTY = "Maze file is empty"
    const val CAVE_FILE_EMPTY = "Cave file is empty"
    const val MAZE_ROW_INTEGER = "Maze row count must be an integer"
    const val MAZE_COLUMN_INTEGER = "Maze column count must be an integer"
    const val CAVE_ROW_INTEGER = "Cave row count must be an integer"
    const val CAVE_COLUMN_INTEGER = "Cave column count must be an integer"
    const val INVALID_MAZE_DIMENSIONS = "Invalid maze dimensions"
    const val INVALID_CAVE_DIMENSIONS = "Invalid cave dimensions"
    const val MAZE_BINARY_ONLY = "Maze matrices can contain only 0 or 1 values"
    const val CAVE_BINARY_ONLY = "Cave matrix can contain only 0 or 1 values"
    const val INITIAL_CHANCE_INTEGER = "Initial chance must be an integer"
    const val AUTO_STEP_DELAY_INTEGER = "Auto step delay must be an integer"
    const val AUTO_STEP_DELAY_POSITIVE = "Auto step delay must be a positive integer"
    const val NO_PATH_EXISTS = "No path exists between the selected points"
    const val NO_MAZE_TO_SAVE = "There is no maze to save"
    const val LOAD_OR_GENERATE_MAZE_BEFORE_SOLVING = "Load or generate a maze before solving it"
    const val LOAD_OR_INITIALIZE_CAVE_BEFORE_STEPPING = "Load or initialize a cave before stepping"
    const val LOAD_OR_INITIALIZE_CAVE_BEFORE_AUTO = "Load or initialize a cave before starting auto mode"
    const val FAILED_TO_LOAD_MAZE = "Failed to load maze"
    const val FAILED_TO_LOAD_BUNDLED_SAMPLE = "Failed to load bundled sample"
    const val FAILED_TO_GENERATE_MAZE = "Failed to generate maze"
    const val FAILED_TO_SOLVE_MAZE = "Failed to solve maze"
    const val FAILED_TO_SAVE_MAZE = "Failed to save maze"
    const val FAILED_TO_TRAIN_AGENT = "Failed to train agent"
    const val FAILED_TO_BUILD_AGENT_ROUTE = "Failed to build agent route"
    const val FAILED_TO_LOAD_CAVE = "Failed to load cave"
    const val FAILED_TO_INITIALIZE_CAVE = "Failed to initialize cave"
    const val FAILED_TO_ADVANCE_CAVE = "Failed to advance cave"
    const val FAILED_TO_START_AUTO = "Failed to start auto mode"
    const val LOAD_OR_GENERATE_MAZE_BEFORE_TRAINING = "Load or generate a maze before training the agent"
    const val TRAIN_AGENT_BEFORE_ROUTE = "Train the agent before building an agent route"
    const val AGENT_ROUTE_BUILD_FAILED = "Agent route could not reach the exit"
    const val WEB_INVALID_MAZE_PAYLOAD = "Invalid maze payload"
    const val WEB_INVALID_REQUEST = "Invalid request"
    const val WEB_UNEXPECTED_SERVER_ERROR = "Unexpected server error"
    const val WEB_MAZE_FILE_REQUIRED = "Maze file is required"

    fun bundledSampleNotFound(sampleName: String): String = "Bundled sample not found: $sampleName"

    fun mazeRowRange(min: Int, max: Int): String = "Maze row count must be between $min and $max"

    fun mazeColumnRange(min: Int, max: Int): String = "Maze column count must be between $min and $max"

    fun caveRowRange(min: Int, max: Int): String = "Cave row count must be between $min and $max"

    fun caveColumnRange(min: Int, max: Int): String = "Cave column count must be between $min and $max"

    fun matrixRowCount(matrixName: String, colCount: Int): String {
        return "Each $matrixName matrix row must contain exactly $colCount values"
    }

    fun mazeFileRowCounts(rows: Int): String {
        return "Maze file must contain $rows rows for right walls and $rows rows for bottom walls"
    }

    fun caveFileRowCount(rows: Int): String = "Cave file must contain $rows rows of cave cells"

    fun rowCountPositive(entityName: String): String = "$entityName row count must be positive"

    fun columnCountPositive(entityName: String): String = "$entityName column count must be positive"

    fun matrixRowCountMatch(entityName: String): String = "$entityName matrix row count must match ${entityName.lowercase()} row count"

    fun rightWallRowCountMatch(): String = "Right wall matrix row count must match maze row count"

    fun bottomWallRowCountMatch(): String = "Bottom wall matrix row count must match maze row count"

    fun matrixColumnCountMatch(matrixName: String): String = "Each $matrixName matrix row must match ${matrixName.substringBefore(' ')} column count"

    fun limitRange(name: String, min: Int, max: Int): String = "$name must be between $min and $max"

    fun chanceRange(min: Int, max: Int): String = "Initial chance must be between $min and $max"

    fun rowMustBeInteger(prefix: String): String = "$prefix row must be an integer"

    fun columnMustBeInteger(prefix: String): String = "$prefix column must be an integer"

    fun rowRange(prefix: String, max: Int): String = "$prefix row must be between 1 and $max"

    fun columnRange(prefix: String, max: Int): String = "$prefix column must be between 1 and $max"
}
