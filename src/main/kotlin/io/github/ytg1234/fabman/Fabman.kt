package io.github.ytg1234.fabman

import io.github.ytg1234.fabman.cli.Mode

class Fabman(val mode: Mode, val args: Array<String>) {
    fun `do`() {
        when (mode) {
            Mode.Help -> doHelp()
            Mode.Setup -> doSetup()
            Mode.Install -> doInstall()
            Mode.Uninstall -> doUninstall()
            Mode.Submit -> doSubmit()
        }
    }

    private fun doHelp() {}
    private fun doSetup() {}
    private fun doInstall() {}
    private fun doUninstall() {}
    private fun doSubmit() {}
}
