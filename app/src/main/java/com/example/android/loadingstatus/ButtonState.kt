package com.example.android.loadingstatus

sealed class ButtonState(var text: String, var fieName: String, var status: String) {
    object Clicked : ButtonState("We are loading", "", "not updating")
    object Loading : ButtonState("We are loading", "", "not updating")
    object Completed : ButtonState("Download", "", "not updating")
}