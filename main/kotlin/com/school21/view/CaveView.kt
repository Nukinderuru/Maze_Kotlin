package com.school21.view

import com.school21.model.Cave

interface CaveView {
    fun renderCave(cave: Cave)

    fun showError(message: String)

    fun showStatus(message: String)
}
