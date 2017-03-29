package site.paulo.localchat.ui.main

import site.paulo.localchat.data.model.ribot.Ribot
import site.paulo.localchat.ui.base.BaseMvpPresenter
import site.paulo.localchat.ui.base.MvpView

object MainContract {

    interface View: MvpView {
        fun showRibots(ribots: List<Ribot>)
        fun showRibotsEmpty()
        fun showError()
    }

    abstract class Presenter: BaseMvpPresenter<View>() {
        abstract fun loadRibots()
    }
}
