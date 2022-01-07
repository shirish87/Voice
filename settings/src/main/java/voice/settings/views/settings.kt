package voice.settings.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import voice.common.compose.VoiceTheme
import voice.settings.R
import voice.settings.SettingsListener
import voice.settings.SettingsViewModel
import voice.settings.SettingsViewState

@Composable
@Preview
private fun SettingsPreview() {
  val viewState = SettingsViewState(
    useDarkTheme = false,
    showDarkThemePref = true,
    resumeOnReplug = true,
    seekTimeInSeconds = 42,
    autoRewindInSeconds = 12,
    dialog = null
  )
  VoiceTheme {
    Settings(viewState, object : SettingsListener {
      override fun close() {}
      override fun toggleResumeOnReplug() {}
      override fun toggleDarkTheme() {}
      override fun seekAmountChanged(seconds: Int) {}
      override fun onSeekAmountRowClicked() {}
      override fun autoRewindAmountChanged(seconds: Int) {}
      override fun onAutoRewindRowClicked() {}
      override fun onLikeClicked() {}
      override fun dismissDialog() {}
      override fun openSupport() {}
      override fun openTranslations() {}
    })
  }
}

@Composable
private fun Settings(viewState: SettingsViewState, listener: SettingsListener) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(R.string.action_settings))
        },
        actions = {
          IconButton(
            onClick = {
              listener.onLikeClicked()
            },
            content = {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.pref_support_title)
              )
            }
          )
        },
        navigationIcon = {
          IconButton(
            onClick = {
              listener.close()
            }
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.close)
            )
          }
        }
      )
    }
  ) {
    Column(Modifier.padding(vertical = 8.dp)) {
      if (viewState.showDarkThemePref) {
        DarkThemeRow(viewState.useDarkTheme, listener::toggleDarkTheme)
      }
      ResumeOnReplugRow(viewState.resumeOnReplug, listener::toggleResumeOnReplug)
      SeekTimeRow(viewState.seekTimeInSeconds) {
        listener.onSeekAmountRowClicked()
      }
      AutoRewindRow(viewState.autoRewindInSeconds) {
        listener.onAutoRewindRowClicked()
      }
      Dialog(viewState, listener)
    }
  }}

@Composable
internal fun Settings(viewModel: SettingsViewModel) {
  val viewState by viewModel.viewState().collectAsState(SettingsViewState.Empty)
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(R.string.action_settings))
        },
        actions = {
          IconButton(
            onClick = {
              viewModel.onLikeClicked()
            },
            content = {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.pref_support_title)
              )
            }
          )
        },
        navigationIcon = {
          IconButton(
            onClick = {
              viewModel.close()
            }
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.close)
            )
          }
        }
      )
    }
  ) {
    Column(Modifier.padding(vertical = 8.dp)) {
      if (viewState.showDarkThemePref) {
        DarkThemeRow(viewState.useDarkTheme, viewModel::toggleDarkTheme)
      }
      ResumeOnReplugRow(viewState.resumeOnReplug, viewModel::toggleResumeOnReplug)
      SeekTimeRow(viewState.seekTimeInSeconds) {
        viewModel.onSeekAmountRowClicked()
      }
      AutoRewindRow(viewState.autoRewindInSeconds) {
        viewModel.onAutoRewindRowClicked()
      }
      Dialog(viewState, viewModel)
    }
  }
}

@Composable
private fun Dialog(
  viewState: SettingsViewState,
  listener: SettingsListener
) {
  val dialog = viewState.dialog ?: return
  when (dialog) {
    SettingsViewState.Dialog.Contribute -> {
      ContributeDialog(
        suggestionsClicked = { listener.openSupport() },
        translationsClicked = { listener.openTranslations() },
        onDismiss = { listener.dismissDialog() }
      )
    }
    SettingsViewState.Dialog.AutoRewindAmount -> {
      AutoRewindAmountDialog(
        currentSeconds = viewState.autoRewindInSeconds,
        onSecondsConfirmed = listener::autoRewindAmountChanged,
        onDismiss = listener::dismissDialog
      )
    }
    SettingsViewState.Dialog.SeekTime -> {
      SeekAmountDialog(
        currentSeconds = viewState.seekTimeInSeconds,
        onSecondsConfirmed = listener::seekAmountChanged,
        onDismiss = listener::dismissDialog
      )
    }
  }
}
