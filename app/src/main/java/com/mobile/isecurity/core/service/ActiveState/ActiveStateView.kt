package com.mobile.isecurity.core.service.ActiveState

interface ActiveStateView {
    interface View {
        fun onUpdateActiveState(state : Int)
    }

    interface Presenter {
        fun updateActiveState(state : Int)
    }
}