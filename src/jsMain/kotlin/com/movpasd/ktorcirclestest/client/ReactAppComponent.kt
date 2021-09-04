package com.movpasd.ktorcirclestest.client

import kotlinx.css.*
import kotlinx.html.*
import react.*
import react.dom.*
import styled.css
import styled.styledCanvas
import styled.styledDiv


val fcReactAppComponent = functionalComponent<ReactAppProps> {

    styledDiv {

        css {
            top = 100.px
            margin(LinearDimension.auto)
            width = 900.px
            height = 600.px
        }

        canvas {
            attrs {
                id = "app_canvas"
                width = "900px"
                height = "600px"
            }
        }

    }

}


external interface ReactAppProps : RProps {

}