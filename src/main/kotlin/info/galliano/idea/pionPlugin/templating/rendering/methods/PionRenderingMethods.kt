package info.galliano.idea.pionPlugin.templating.rendering.methods

import info.galliano.idea.pionPlugin.psi.method.defenition.MethodCallDefinition

object PionRenderingMethods {
    val methods = arrayOf(
        MethodCallDefinition("\\Peony\\Engine\\EngineInterface", "render", 0),
        MethodCallDefinition(
            "\\Peony\\Renderable\\PredefinedRenderable",
            "__construct",
            0
        )
    )
}