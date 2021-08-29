import kotlinx.html.*
import react.*
import react.dom.*


class App : RComponent<AppProps, AppState>() {

    override fun RBuilder.render() {
        div {
            attrs.id = "app_root"
        }
    }

}

external interface AppProps : RProps {

}

class AppState : RState {

}