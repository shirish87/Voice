package de.ph1b.audiobook.features.bookPlaying.selectchapter

import de.ph1b.audiobook.data.markForPosition
import de.ph1b.audiobook.data.repo.BookRepository
import de.ph1b.audiobook.playback.PlayerController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class SelectChapterViewModel
@Inject constructor(
  private val bookRepository: BookRepository,
  private val player: PlayerController
) {

  private val scope = MainScope()

  private val _viewEffects = MutableSharedFlow<SelectChapterViewEffect>(extraBufferCapacity = 1)
  val viewEffects: Flow<SelectChapterViewEffect> get() = _viewEffects

  lateinit var bookId: UUID

  fun viewState(): SelectChapterViewState {
    val book = bookRepository.bookById(bookId)

    if (book == null) {
      Timber.d("no book found for $bookId. CloseScreen")
      _viewEffects.tryEmit(SelectChapterViewEffect.CloseScreen)
      return SelectChapterViewState(emptyList(), null)
    }

    val chapterMarks = book.content.chapters.flatMap {
      it.chapterMarks
    }
    val currentMark = book.content.currentChapter.markForPosition(book.content.positionInChapter)
    val selectedIndex = chapterMarks.indexOf(currentMark)
    return SelectChapterViewState(chapterMarks, selectedIndex.takeUnless { it == -1 })
  }

  fun chapterClicked(index: Int) {
    scope.launch {
      val book = bookRepository.bookById(bookId) ?: return@launch
      var currentIndex = -1
      book.content.chapters.forEach { chapter ->
        chapter.chapterMarks.forEach { mark ->
          currentIndex++
          if (currentIndex == index) {
            player.setPosition(mark.startMs, chapter.uri)
            _viewEffects.tryEmit(SelectChapterViewEffect.CloseScreen)
            return@launch
          }
        }
      }
    }
  }
}
